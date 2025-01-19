### 主要功能
- 常用的 jenkinsfile 方法
- 常用的共享库方法

### 使用方法
- 安装插件 `Pipeline: Groovy` 和 `Pipeline: Groovy Libraries`。
- 在全局配置中 `Global Trusted Pipeline Libraries` 配置，其中 Name 就是 jenkinsfile 中的共享库的应用
- 在 jenkinfile 中上来加载 `@Library('demo-shared-library') _` 就是上面的 Name
- 在 jenkins 创建任务，选择 `pipeline script from SCM`,选择指定的仓库和分支
- 指定脚本路径，就是需要执行的脚本.
