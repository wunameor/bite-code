# 在云服务器上直接构建：docker compose build app
# 不声明 # syntax 前端镜像：避免构建时额外访问 Docker Hub 拉取 dockerfile frontend。
# Maven 依赖通过 deploy/maven-settings.xml 走国内镜像，并用 BuildKit 缓存挂载复用本地仓库。

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY deploy/maven-settings.xml /root/.m2/settings.xml
COPY pom.xml ./
COPY src ./src
# 不加 -q：首次构建要下载数百 MB 依赖，需要 INFO 输出才能区分“下载中”和“真卡住”
# --no-transfer-progress：不打印逐文件进度条，避免日志刷屏
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn -B --no-transfer-progress -Dmaven.test.skip=true package

FROM eclipse-temurin:17-jre-jammy
ENV TZ=Asia/Shanghai
WORKDIR /app
RUN groupadd --system app \
    && useradd --system --gid app --home-dir /app --no-create-home app \
    && mkdir -p /app/uploads /app/logs \
    && chown -R app:app /app
COPY --from=build /build/target/lottery-system.jar /app/app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-Duser.timezone=Asia/Shanghai", "-jar", "/app/app.jar"]
