# easyfx

JavaFX 简易框架，Java&JavaFX交流群 623110424。

**使用maven引入**

```xml
<dependency>
    <groupId>xyz.yuelai</groupId>
    <artifactId>easyfx</artifactId>
    <version>1.1.0</version>
</dependency>
    
<!-- 使用了coding制品库发布maven包，所以设置以下coding的仓库 -->
<repositories>
    <repository>
        <id>randore-easyfx-easyfx</id>
        <name>easyfx</name>
        <url>https://randore-maven.pkg.coding.net/repository/easyfx/easyfx/</url>
    </repository>
</repositories>
```

**直接引入jar包**

[1.1.0-release]: https://github.com/xizi110/easyfx/releases

## 使用方法

1. 新建一个fxml文件，不指定`fx:controller`。

   ```xml
   <StackPane xmlns="http://javafx.com/javafx/" prefWidth="800" prefHeight="500">
       <VBox alignment="center" spacing="10">
           <Label text="Hello JavaFX">
               <font>
                   <Font size="20"/>
               </font>
           </Label>
       </VBox>
   </StackPane>
   ```

2. 新建一个类，继承`xyz.yuelai.View`类，实现`public String fxml()`方法，此方法指定fxml文件位置。可以指定绝对路径，也可以指定相对路径。

   fxml文件路径如图所示

   ![fxml文件路径](https://github.com/xizi110/easyfx/blob/1.1.0/image-20201009170359938.png)

   ```java
   public class MainView extends View {
   
       @Override
       public String fxml() {
           // 绝对路径
           // return "/xyz/yuelai1/view/Main.fxml";
           
           // 相对路径
           return "Main.fxml";
       }
   
   }
   ```

3. 在实现了Application的子类start方法中，使用`View.createView()`静态方法创建视图

   ```java
   @Override
   public void start(Stage primaryStage) throws Exception{
       MainView view = View.createView(MainView.class);
       Scene scene = new Scene(view.getRoot());
       primaryStage.setTitle("Hello World");
       primaryStage.setScene(scene);
       primaryStage.show();
   }
   ```

4. 启动程序
   ![image-20201009170622248](https://github.com/xizi110/easyfx/blob/1.1.0/image-20201009170622248.png)

## View类中的简便方法

| 方法                                                         | 解释                                         |
| ------------------------------------------------------------ | -------------------------------------------- |
| `getWindow()`                                                | 获取当前视图对应的窗体，未显示窗体之前为null |
| `createView(Class<T> viewClass)`                             | 根据view类创建view对象                       |
| `getRoot()`                                                  | 获取当前视图的根节点                         |
| `createWindow(Class<? extends View> viewClass)`              | 创建窗口，暂不显示，用户可以对窗体进行设置   |
| `alert(String message, Alert.AlertType alertType)`           | 弹窗提示，可以指定弹窗类型                   |
| `showNotification(String message, Notification.NotificationType type)` | 窗体右上角弹窗通知                           |
| `showNotificationAutoClose(String message, Notification.NotificationType type)` | 窗体右上角弹窗通知，可自动关闭               |
| `runAsync(Task<?> task)`                                     | 异步执行任务                                 |
| `sendMessageWithAsync(String receiverName, Object... data)`  | 异步发送消息给其他 View 方法                 |

## 多个 view 之间的通信

一个窗体包含一个scene，一个scene可以包含一个或多个view。多个view之间的通信使用`sendMessageWithAsync(String receiverName, Object... data)`方法。参数`receiverName`指定接收者的名字，`Object... data`为接收者的方法参数。被注解`@Receiver`注释的方法，称为接收者，`@Receiver`注解可以指定name，即为`sendMessageWithAsync`方法的`receiverName`参数

发送消息：按钮绑定一个点击事件，点击之后调用add方法，方法中发送了一个信息给接收名为`"add"`的接收者

```java
@FXML
private void add(){
    sendMessageWithAsync("add");
}
```

接收者对应的view类中，`@Receiver(name = "add")`此注解可以接受发送给接收名为`"add"`的接收者的信息

```java
@Receiver(name = "add")
private void add() {
    number.setText("" + ++num);
}
```

一个view类中可以有接收名一样的接收者，如果不设置接收名，则默认为`""`，只能接收`sendMessageWithAsync("")`这类信息。











