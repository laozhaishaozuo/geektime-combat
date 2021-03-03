# 第一周作业

## 页面跳转

通过自研 Web MVC 框架实现（可以自己实现）一个用户注册，forward 到一个成功的页面（JSP 用法）


https://github.com/laozhaishaozuo/geektime-combat/week01/simple-user-platform/simple-user-web

访问地址 :http://localhost:8080/simple-user-web/user/register




## 数据库实现

通过 Controller -> Service -> Repository 实现（数据库实现）
https://github.com/laozhaishaozuo/geektime-combat/week01/simple-user-platform/simple-user-db

访问地址 :http://localhost:8080/simple-user-db/user/register


## 运行

1. 克隆项目到本地 `git clone https://github.com/laozhaishaozuo/geektime-combat.git`
2. 在 week01/simple-user-platform 文件夹下运行 `mvn clean install`
3. 将 `simple-user-web/target/simple-user-web.war` 和 `simple-user-db/target/simple-user-db.war` 放到tomcat中运行
