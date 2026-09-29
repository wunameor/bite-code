#!/usr/bin/env bash
# live-blog 一键初始化：拉取代码 + 部署，可在任意目录执行
#   首次:  curl -fsSL https://raw.githubusercontent.com/wunameor/bite-code/master/Java118/JavaEE/advanced/spring-blog-demo/init.sh | sudo bash
#   更新:  在 spring-blog-demo 目录内再次执行 sudo bash init.sh（自动 git pull + 重新部署）
# 说明: 服务器到 Gitee 的 443 被阻断，走 GitHub；项目是 bite-code 大仓库的子目录，
#       用 sparse + partial clone 只下载本项目，省流量
set -euo pipefail

export GIT_TERMINAL_PROMPT=0   # 需要输密码时直接报错，避免挂起

REPO_URL="${REPO_URL:-https://github.com/wunameor/bite-code.git}"
REPO_DIR_NAME="bite-code"
PROJECT_SUBDIR="Java118/JavaEE/advanced/spring-blog-demo"

SCRIPT_DIR="$(cd "$(dirname "$(readlink -f "$0")")" && pwd)"

if [ -f "$SCRIPT_DIR/docker-compose.yml" ]; then
    # init.sh 在仓库内运行：直接更新所在仓库
    APP_DIR="$SCRIPT_DIR"
    REPO_DIR="$(git -C "$APP_DIR" rev-parse --show-toplevel)"
    echo "==> 更新代码: $REPO_DIR"
    git -C "$REPO_DIR" pull --ff-only
else
    # 在仓库外运行：克隆到当前目录 ./bite-code（已存在则只更新），稀疏检出本项目
    REPO_DIR="$PWD/$REPO_DIR_NAME"
    if [ -d "$REPO_DIR/.git" ]; then
        echo "==> 更新代码: $REPO_DIR"
        git -C "$REPO_DIR" pull --ff-only
    else
        echo "==> 克隆仓库(仅本项目子目录): $REPO_DIR"
        git clone --depth 1 --filter=blob:none --sparse "$REPO_URL" "$REPO_DIR"
        git -C "$REPO_DIR" sparse-checkout set "$PROJECT_SUBDIR"
    fi
    APP_DIR="$REPO_DIR/$PROJECT_SUBDIR"
fi

cd "$APP_DIR"
bash setup.sh
