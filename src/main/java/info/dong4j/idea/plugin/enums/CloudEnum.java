package info.dong4j.idea.plugin.enums;

import org.jetbrains.annotations.Contract;

/**
 * <p>Company: 科大讯飞股份有限公司-四川分公司</p>
 * <p>Description: ${description}</p>
 *
 * @author dong4j
 * @date 2019-03-17 00:15
 * @email sjdong3@iflytek.com
 */
public enum CloudEnum {
    WEIBO_CLOUD(0, "微博"),
    ALIYUN_CLOUD(1, "阿里云"),
    QINIU_CLOUD(2, "七牛云"),
    WANGYI_CLOUD(3, "网易云"),
    BAIDU_CLOUD(4, "百度云"),
    JINGDONG_CLOUD(5, "京东云"),
    YOUPAI_CLOUD(6, "又拍云"),
    SM_MS_CLOUD(7, "sm.ms"),
    IMGUR_CLOUD(8, "Imgur"),
    U_CLOUD(9, "Ucloud"),
    QING_CLOUD(10, "QIngCloud"),
    CUSTOMIZE(11, "自定义");

    public int index;
    public String title;

    CloudEnum(int index, String title) {
        this.index = index;
        this.title = title;
    }

    @Contract(pure = true)
    public int getIndex() {
        return index;
    }

    @Contract(pure = true)
    public String getTitle() {
        return title;
    }
}
