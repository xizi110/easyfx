package xyz.yuelai;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Preloader;

/**
 * @author zhong
 * @date 2020-10-10 13:40:05 周六
 */
public abstract class FXApplication extends Application {

    /**
     * 指定预加载器
     *
     * @param preloadClass 预加载器类
     * @param appClass     application 类
     * @param args         命令行参数
     */
    public static void launch(Class<? extends Preloader> preloadClass, Class<? extends Application> appClass, String... args) {
        LauncherImpl.launchApplication(appClass,preloadClass, args);
    }

    public static void setElementStyleEnable(boolean enable){
        View.ELEMENT_STYLE = enable;
    }
}
