#!/usr/bin/env bash
# live-blog 部署脚本（Ubuntu 云服务器，幂等可重复执行）
# 一般无需直接调用，由 init.sh 封装: sudo bash init.sh
set -euo pipefail

APP_DIR="$(cd "$(dirname "$0")" && pwd)"
MYSQL_DISK="/opt/live-blog-mysql"        # MySQL 数据挂载点
MYSQL_IMG="/opt/live-blog-mysql.img"     # 5G 限额 loop 文件
MYSQL_SIZE_GB=5
DB_PASS='@L1Y2c3@'

# ---------- 1. 安装 Docker（已装则跳过） ----------
if ! command -v docker >/dev/null 2>&1; then
    echo "==> [1/4] 安装 Docker ..."
    apt-get update
    apt-get install -y ca-certificates curl
    install -m 0755 -d /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
    chmod a+r /etc/apt/keyrings/docker.asc
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] \
      https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" \
      > /etc/apt/sources.list.d/docker.list
    apt-get update
    apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
    systemctl enable --now docker
else
    echo "==> [1/4] Docker 已安装: $(docker --version)"
fi

# ---------- 2. 创建/挂载 5G 限额磁盘（仅 MySQL 数据使用） ----------
if findmnt -no TARGET "$MYSQL_DISK" >/dev/null 2>&1; then
    echo "==> [2/4] 限额盘已挂载: $MYSQL_DISK"
else
    echo "==> [2/4] 初始化 ${MYSQL_SIZE_GB}G 限额盘 ..."
    if [ ! -f "$MYSQL_IMG" ]; then
        dd if=/dev/zero of="$MYSQL_IMG" bs=1M count=0 seek=$((MYSQL_SIZE_GB * 1024)) status=none
        mkfs.ext4 -q "$MYSQL_IMG"
    fi
    mkdir -p "$MYSQL_DISK"
    grep -qF "$MYSQL_IMG" /etc/fstab || echo "$MYSQL_IMG $MYSQL_DISK ext4 loop,defaults 0 0" >> /etc/fstab
    mount "$MYSQL_DISK"
    chown 999:999 "$MYSQL_DISK"   # 无条件确保 mysql 容器用户可写（全新盘属主是 root）

    # 迁移旧数据（compose 升级前用 ./mysql-data 目录）
    if [ -d "$APP_DIR/mysql-data" ] && [ -n "$(ls -A "$APP_DIR/mysql-data" 2>/dev/null)" ] \
       && [ -z "$(ls -A "$MYSQL_DISK" 2>/dev/null | grep -v lost+found)" ]; then
        echo "    检测到旧数据 ./mysql-data，迁移中..."
        cp -a "$APP_DIR/mysql-data/." "$MYSQL_DISK/"
        chown -R 999:999 "$MYSQL_DISK"
        mv "$APP_DIR/mysql-data" "$APP_DIR/mysql-data.bak"
    fi
fi

# ---------- 3. 构建并启动 ----------
echo "==> [3/4] docker compose up -d --build ..."
cd "$APP_DIR"
docker compose up -d --build
docker image prune -f

# ---------- 4. 配置每日 06:00 数据重置 cron（幂等） ----------
echo "==> [4/4] 配置定时重置 cron ..."
CRON_JOB="0 6 * * * cd ${APP_DIR} && docker compose exec -T mysql mysql -uroot -p'${DB_PASS}' < sql/99-reset.sql"
( crontab -l 2>/dev/null | grep -vF "99-reset.sql"; echo "$CRON_JOB" ) | crontab -

echo ""
echo "==> 部署完成! 容器状态:"
docker compose ps
echo "    本机直连: http://127.0.0.1:25000/blog_login.html (域名反代走服务器 Nginx/1Panel)"
