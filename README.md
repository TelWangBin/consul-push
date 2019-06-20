azy-consul-pusher：推送本地配置文件到consul配置中心使用说明

1、请先到\consul-push\consul

启动consul-start.bat 

2、在resources文件夹下新增配置文件需修改以下内容

1）、新增配置文件的命名规则举例说明：

新增配置文件名称为uaa-dev.yml，对应的consul配置中心得名称为config/uaa,dev/data

2）、修改azy-version.yml中的内容，添加新增的配置文件的版本号

例：配置文件名称为uaa-dev.yml，则新增的内容为uaa-dev-version: 1.0

3、修改现有的配置文件，需同步修改已存在的版本号

4、源码通俗易懂，若有任何不明之处请读源码