package moocollege.cn.commonbannerview.banner;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by zsd on 2017/7/31 10:52
 * desc:通过这个类去改变viewpager的切换速率
 */

public class BannerScroller extends Scroller {

    //动画持续的时间 页面切换所持续的时间
    private int mScrollerDuration = 750;


    /**
     * 设置切换页面持续的时间
     * @param scrollerDuration
     */
    public void setScrollerDuration(int scrollerDuration) {
        this.mScrollerDuration = scrollerDuration;
    }

    public BannerScroller(Context context) {
        super(context);
    }

    public BannerScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public BannerScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy,int duration) {
        super.startScroll(startX, startY, dx, dy,mScrollerDuration);
    }


}
