#!/usr/bin/env bash
# 服务器首次安装/更新：只检出 monorepo 中的 lottery-system 子目录，
# 并把固定部署路径 /www/bite/lottery-system 指向该子目录，然后调用一键部署脚本。
#
# 首次安装：
#   bash <(curl -fsSL https://raw.githubusercontent.com/wunameor/bite-code/master/project/lottery-system/deploy/install-server.sh)
#
# 后续更新（在 /www/bite/lottery-system 下执行）：
#   ./deploy/install-server.sh
#
# 可用环境变量覆盖：
#   DEPLOY_DIR   固定部署路径，默认 /www/bite/lottery-system
#   REPO_DIR     monorepo 缓存目录，默认 /var/lib/lottery-system/repo
#   REPO_URL     仓库地址，默认 GitHub；可换成 Gitee 镜像
set -euo pipefail

DEPLOY_DIR="${DEPLOY_DIR:-/www/bite/lottery-system}"
REPO_DIR="${REPO_DIR:-/var/lib/lottery-system/repo}"
REPO_URL="${REPO_URL:-https://github.com/wunameor/bite-code.git}"
SUBDIR="project/lottery-system"

if ! command -v git >/dev/null 2>&1; then
    echo "缺少命令：git" >&2
    exit 1
fi

if [[ -d "$REPO_DIR" && ! -d "$REPO_DIR/.git" ]]; then
    echo "$REPO_DIR 已存在但不是 Git 仓库，无法安全更新。" >&2
    echo "请确认后删除该目录再重试：rm -rf $REPO_DIR" >&2
    exit 1
fi

# 稀疏检出规则可能因为上次中断而没有设置，每次都要确保包含目标子目录
apply_sparse() {
    if ! git -C "$REPO_DIR" sparse-checkout set "$SUBDIR" >/dev/null 2>&1; then
        echo "[install] 重新初始化 cone 模式稀疏检出"
        git -C "$REPO_DIR" sparse-checkout init --cone
        git -C "$REPO_DIR" sparse-checkout set "$SUBDIR"
    fi
}

clone_sparse() {
    mkdir -p "$(dirname "$REPO_DIR")"
    echo "[install] 稀疏检出 $REPO_URL"
    if ! git clone --depth 1 --filter=blob:none --sparse "$REPO_URL" "$REPO_DIR"; then
        echo "[install] 当前 Git 服务端不支持 --filter，退回普通浅克隆"
        rm -rf "$REPO_DIR"
        git clone --depth 1 --sparse "$REPO_URL" "$REPO_DIR"
    fi
    apply_sparse
}

CURRENT_URL=""
if [[ -d "$REPO_DIR/.git" ]]; then
    CURRENT_URL="$(git -C "$REPO_DIR" remote get-url origin 2>/dev/null || true)"
    if [[ -z "$CURRENT_URL" ]]; then
        echo "$REPO_DIR 缺少 origin 远端，无法判断代码来源。" >&2
        echo "请确认后删除该目录再重试：rm -rf $REPO_DIR" >&2
        exit 1
    fi
fi

if [[ -z "$CURRENT_URL" ]]; then
    clone_sparse
elif [[ "$CURRENT_URL" != "$REPO_URL" ]]; then
    # .env 位于缓存仓库内部，重建缓存前先备份，避免密钥丢失
    ENV_BACKUP=""
    if [[ -f "$DEPLOY_DIR/.env" ]]; then
        ENV_BACKUP="$(mktemp)"
        cp "$DEPLOY_DIR/.env" "$ENV_BACKUP"
        echo "[install] 已备份 .env：$DEPLOY_DIR/.env"
    fi
    echo "[install] 缓存仓库地址不一致：$CURRENT_URL"
    echo "[install] 改为重新检出：$REPO_URL"
    rm -rf "$REPO_DIR"
    clone_sparse
    if [[ -n "$ENV_BACKUP" ]]; then
        cp "$ENV_BACKUP" "$DEPLOY_DIR/.env"
        chmod 600 "$DEPLOY_DIR/.env" 2>/dev/null || true
        rm -f "$ENV_BACKUP"
        echo "[install] 已恢复 .env"
    fi
else
    echo "[install] 更新代码"
    apply_sparse
    git -C "$REPO_DIR" pull --ff-only
    apply_sparse
fi

TARGET="$REPO_DIR/$SUBDIR"
if [[ ! -d "$TARGET" ]]; then
    echo "未找到子目录：$TARGET" >&2
    echo "缓存仓库：${CURRENT_URL:-未知}" >&2
    echo "当前提交：$(git -C "$REPO_DIR" log --oneline -1 2>/dev/null || echo 未知)" >&2
    echo "当前稀疏规则：" >&2
    git -C "$REPO_DIR" sparse-checkout list 2>/dev/null | sed 's/^/  /' >&2 || true
    echo "仓库根目录内容：" >&2
    git -C "$REPO_DIR" ls-tree --name-only HEAD 2>/dev/null | sed 's/^/  /' >&2 || true
    echo "请确认默认分支包含 $SUBDIR，或检查 REPO_URL。" >&2
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
