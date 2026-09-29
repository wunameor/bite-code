# 构建阶段：在容器内用 Maven 打包，服务器/本地都无需上传 jar
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# 先只拷贝 pom.xml 下载依赖，源码变动时该层可复用缓存
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -B dependency:go-offline

# 再拷贝源码编译打包
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -B -DskipTests package

# 运行阶段：只带 JRE，镜像更小
FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

WORKDIR /app
COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
