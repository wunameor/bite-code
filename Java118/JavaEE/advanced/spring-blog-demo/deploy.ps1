# 一键部署：Windows 本地打包源码 -> 上传 Ubuntu 云服务器 -> 服务器上 Docker 构建并启动（无需上传 jar）
# 用法: .\deploy.ps1 -ServerHost <云服务器IP>
#       .\deploy.ps1 -ServerHost 1.2.3.4 -ServerUser root -RemoteDir /opt/live-blog
param(
    [Parameter(Mandatory = $true)]
    [string]$ServerHost,
    [string]$ServerUser = "root",
    [string]$RemoteDir  = "/www/bite/live-blog"
)

$ErrorActionPreference = "Stop"
$projDir = $PSScriptRoot
$tarFile = Join-Path $env:TEMP "live-blog-src.tar.gz"

Write-Host "==> 1/3 打包源码 (排除 target/.git/.idea/logs)..."
if (Test-Path $tarFile) { Remove-Item $tarFile }
tar -czf $tarFile -C $projDir `
    --exclude="./target" --exclude="./.git" --exclude="./.idea" --exclude="./logs" `
    --exclude="./mysql-data" --exclude="./mysql-data.bak" .
$sizeMB = [math]::Round((Get-Item $tarFile).Length / 1MB, 2)
Write-Host "    包大小: ${sizeMB} MB"

Write-Host "==> 2/3 上传到 ${ServerUser}@${ServerHost}..."
scp $tarFile "${ServerUser}@${ServerHost}:/tmp/live-blog-src.tar.gz"

Write-Host "==> 3/3 服务器上解压并执行 setup.sh 部署..."
$remoteCmd = "mkdir -p $RemoteDir && " +
    "tar -xzf /tmp/live-blog-src.tar.gz -C $RemoteDir && " +
    "cd $RemoteDir && " +
    "sed -i 's/\r$//' setup.sh && " +
    "bash setup.sh"
ssh "${ServerUser}@${ServerHost}" $remoteCmd

if ($LASTEXITCODE -eq 0) {
    Write-Host "==> 部署完成! 端口仅绑定 127.0.0.1:25000，请通过域名反代访问 https://live-blog.774821.xyz" -ForegroundColor Green
} else {
    Write-Host "==> 部署失败，检查上方输出" -ForegroundColor Red
    exit 1
}
