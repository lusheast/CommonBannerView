package moocollege.cn.commonbannerview.banner;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsd on 2017/7/28 16:44
 * desc:通用的广告位
 */

public class BannerViewPager extends ViewPager {


    //自定义的Adapter
    private BannerAdapter mAdapter;
    //发送消息的msg
    private final int SCROLL_MSG = 0X0011;
    //实现自动轮播 页面切换间隔时间 毫秒 这个是默认值
    private int defaultDurationTime = 3500;
    //自定义的改变页面切换速率的Scroller 就是改变页面切换的时候持续的时候
    private BannerScroller mBannerScroller;
    //优化内存，不用每次都创建view，类似于listView的复用机制
    private List<View> mConvertViews;
    //当前Activity
    private Activity mActivity;
    //是否可以滚动 默认可以滚动 只有一张图片的时候，是不能自动滚动的
    private boolean isScroll = true;


    private Handler mHandler;


    public BannerViewPager(Context context) {
        super(context, null);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = (Activity) context;
        //改变viewpager的切换速率，源码中是通过mScroller来改变的
        //由于mScroller在源码中是私有的变量，我们是拿不到的,所以我们通过反射来拿这个mScroller
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            mBannerScroller = new BannerScroller(context);
            //mScroller.set()中两个参数 第一个参数代表当前属性在哪个类 第二个参数代表要设置的值
            mScroller.set(this, mBannerScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mConvertViews = new ArrayList<>();
        initHandler();
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //每隔多长时间 我们切换到下一页
                setCurrentItem(getCurrentItem() + 1);
                //不断地循环执行
                startRoll();
            }
        };
    }

    /**
     * 设置切换页面持续的时间
     *
     * @param scrollerDuration
     */
    public void setScrollerDuration(int scrollerDuration) {
        mBannerScroller.setScrollerDuration(scrollerDuration);
    }


    public void setAdapter(BannerAdapter adapter) {
        this.mAdapter = adapter;
        //设置viewpager的Adapter
        setAdapter(new CommonBannerViewPagerAdapter());

    }


    /**
     * 销毁handler 停止发送消息 解决内存泄漏的问题
     * 如果不销毁当前handler,由于handler生命周期大于ctivity
     * 会导致activity关闭后，handler还在不停地发送消息
     */
    @Override
    protected void onDetachedFromWindow() {
       if (mHandler!=null){
           //销毁handler的生命周期
           mHandler.removeMessages(SCROLL_MSG);
           mHandler = null;
           //解除绑定
           mActivity.getApplication().unregisterActivityLifecycleCallbacks(mActivitylifecycleCallbacks);
       }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        if (mAdapter != null) {
            initHandler();
            startRoll();
            // 管理Activity的生命周期
            mActivity.getApplication().registerActivityLifecycleCallbacks(mActivitylifecycleCallbacks);
        }
        super.onAttachedToWindow();
    }




    public void startRoll() {
        //adapter不能为空
        if (mAdapter == null)
            return;
        //判断是不是只有一条数据
        isScroll = mAdapter.getCount() != 1;
        if (isScroll && mHandler != null) {
            //清除消息
            mHandler.removeMessages(SCROLL_MSG);
            //发送消息 延迟时间 自己定义 有一个默认的 这里设置的是一个默认的
            mHandler.sendEmptyMessageDelayed(SCROLL_MSG, defaultDurationTime);
        }
    }

    /**
     * @return
     */
    public View getConvertView() {
        for (int i = 0; i < mConvertViews.size(); i++) {
            //获取没有添加在Viewpager里面的
            if (mConvertViews.get(i).getParent() == null) {
                return mConvertViews.get(i);
            }
        }
        return null;
    }

    /**
     * 为Viewpager设置Adapter
     */
    private class CommonBannerViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            //保证了可以无限的轮播
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * 创建ViewPager条目回调的方法
         */
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            //采用Adapter设计模式 复用机制，每次来的时候我们把上一步销毁的view带来
            View bannerItemView = mAdapter.getView(position % mAdapter.getCount(), getConvertView());
            //将该view添加到viewpager里面，这里的container就是viewpager
            container.addView(bannerItemView);
            bannerItemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.click(position);
                    }
                }
            });
            return bannerItemView;
        }

        /**
         * 销毁ViewPager条目回调的方法
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            //每次销毁后，让mConvertView = object
            mConvertViews.add((View) object);
        }
    }

    //设置点击回调监听
    private BannerItemClickListener mListener;

    public void setOnBannerItemClickListener(BannerItemClickListener listener) {
        this.mListener = listener;
    }

    public interface BannerItemClickListener {
        public void click(int position);
    }

    //管理Activity的声明周期
    Application.ActivityLifecycleCallbacks mActivitylifecycleCallbacks = new BannerActivityLifecycleCallBack() {
        @Override
        public void onActivityResumed(Activity activity) {
            //是不是监听的是当前Activity的生命周期
            if (activity == mActivity) {
                //开启轮播
                mHandler.sendEmptyMessageDelayed(SCROLL_MSG, defaultDurationTime);
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if (activity == mActivity) {
                //停止轮播
                mHandler.removeMessages(SCROLL_MSG);
            }
        }
    };
}
