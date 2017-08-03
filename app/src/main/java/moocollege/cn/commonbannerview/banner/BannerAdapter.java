package moocollege.cn.commonbannerview.banner;

import android.view.View;

/**
 * Created by zsd on 2017/7/31 08:52
 * desc:
 */

public abstract class BannerAdapter {

    //根据位置获取viewpager里面的子view
    public abstract View getView(int position,View mConvertView);
    //获取轮播的数量
    public abstract int getCount();
    //根据位置获取广告位的描述
    public String getBannerDesc(int position){
        return "";
    }

}
