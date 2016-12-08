# QrCodeScanner

#### 背景
帮朋友实现一个商户二维码生成器
通过发过来的Excel读取其中的信息，然后生成二维码，并添加水印，最后覆盖到背景图

#### 准备
一份excel表格
一张需要生成的背景图

#### 原理
zxing二维码的lib，读取excel的lib，通过这两个包就可以实现该功能了，具体看代码

#### 附注
可以生成jar包，然后在当前目录创建资源文件（代码中写死的origin，文件名称都是test.xls test.png）然后通过 java -jar XX.jar。就可以了。
