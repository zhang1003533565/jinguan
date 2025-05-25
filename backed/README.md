# Suidao Backend

这是一个基于 Spring Boot 和 Java 21 的后端项目。

## 技术栈

- Java 21
- Spring Boot 3.2.3
- Maven

## 项目结构

```
src/
  ├── main/
  │   ├── java/
  │   │   └── com/
  │   │       └── example/
  │   │           └── suidao/
  │   │               ├── controller/
  │   │               │   └── HelloController.java
  │   │               └── SuidaoApplication.java
  │   └── resources/
  │       └── application.yml
```

## 运行项目

确保你的系统已安装：
- Java 21 或更高版本
- Maven

### 本地运行

1. 克隆项目到本地
2. 在项目根目录执行：
```bash
mvn spring-boot:run
```

### 打包部署

1. 在项目根目录执行：
```bash
mvn clean package
```
2. 运行生成的jar包：
```bash
java -jar target/suidao-1.0.0.jar
```

## API 接口

- 测试接口：`GET http://localhost:8080/api/hello` 