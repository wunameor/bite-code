#!/usr/bin/env bash
# 服务器首次安装/更新：只检出 monorepo 中的 lottery-system 子目录，
# 并把固定部署路径 /www/bite/lottery-system 指向该子目录，然后调用一键部署脚本。
#
# 首次安装：
#   bash <(curl -fsSL https://gitee.com/wunameor/bite-code/raw/master/project/lottery-system/deploy/install-server.sh)
#
# 后续更新（在 /www/bite/lottery-system 下执行）：
#   ./deploy/install-server.sh
#
# 可用环境变量覆盖：
#   DEPLOY_DIR   固定部署路径，默认 /www/bite/lottery-system
#   REPO_DIR     monorepo 缓存目录，默认 /var/lib/lottery-system/repo
#   REPO_URL     仓库地址，默认 Gitee
set -euo pipefail

DEPLOY_DIR="${DEPLOY_DIR:-/www/bite/lottery-system}"
REPO_DIR="${REPO_DIR:-/var/lib/lottery-system/repo}"
REPO_URL="${REPO_URL:-https://gitee.com/wunameor/bite-code.git}"
SUBDIR="project/lottery-system"

if ! command -v git >/dev/null 2>&1; then
    echo "缺少命令：git" >&2
    exit 1
fi

clone_sparse() {
    mkdir -p "$(dirname "$REPO_DIR")"
    echo "[install] 稀疏检出 $REPO_URL"
    if ! git clone --depth 1 --filter=blob:none --sparse "$REPO_URL" "$REPO_DIR"; then
        echo "[install] 当前 Git 服务端不支持 --filter，退回普通浅克隆"
        rm -rf "$REPO_DIR"
        git clone --depth 1 --sparse "$REPO_URL" "$REPO_DIR"
    fi
    if ! git -C "$REPO_DIR" sparse-checkout set "$SUBDIR"; then
        echo "[install] 重新初始化 cone 模式稀疏检出"
        git -C "$REPO_DIR" sparse-checkout init --cone
        git -C "$REPO_DIR" sparse-checkout set "$SUBDIR"
    fi
}

if [[ ! -d "$REPO_DIR/.git" ]]; then
    clone_sparse
else
    echo "[install] 更新代码"
    git -C "$REPO_DIR" pull --ff-only
fi

TARGET="$REPO_DIR/$SUBDIR"
if [[ ! -d "$TARGET" ]]; then
    echo "未找到子目录：$TARGET" >&2
    echo "请检查仓库分支是否包含 $SUBDIR" >&2
    exit 1
fi

if [[ -L "$DEPLOY_DIR" ]]; then
    CURRENT_TARGET="$(readlink -f "$DEPLOY_DIR" 2>/dev/null || true)"
    DESIRED_TARGET="$(readlink -f "$TARGET")"
    if [[ "$CURRENT_TARGET" != "$DESIRED_TARGET" ]]; then
        echo "[install] 更新部署路径软链接：$DEPLOY_DIR"
        ln -sfn "$TARGET" "$DEPLOY_DIR"
    fi
elif [[ -e "$DEPLOY_DIR" ]]; then
    echo "$DEPLOY_DIR 已存在且不是软链接，为避免覆盖已中止。" >&2
    echo "请先备份并移走该目录，或设置 DEPLOY_DIR 指向其他路径。" >&2
    exit 1
else
    echo "[install] 创建部署路径：$DEPLOY_DIR"
    mkdir -p "$(dirname "$DEPLOY_DIR")"
    ln -s "$TARGET" "$DEPLOY_DIR"
fi

exec bash "$DEPLOY_DIR/deploy/deploy-server.sh" "$@"
