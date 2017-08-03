package moocollege.cn.commonbannerview.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import moocollege.cn.commonbannerview.R;

/**
 * Created by zsd on 2017/7/31 13:37
 * desc:
 */

public class BannerView extends RelativeLayout {

    //banner的viewpager
    private BannerViewPager mBannerVp;
    //广告位的描述
    private TextView mTextBannerDesc;
    //广告位底部布局
    private LinearLayout mDotContainerView;
    //广告位的Adapter
    private BannerAdapter mBannerAdapter;
    //上下文
    private Context mContext;
    //当前点选中的Drawable
    private Drawable mIndicatorSelectedDrawable;
    //默认的Drawable
    private Drawable mIndicatorNormalDrawable;
    //当前的点的位置
    private int mCurrentPosition = 0;
    //点的位置 默认在左边
    private int mDotGravity = 1;
    //点的大小 默认8dp
    private int mDotSize = 8;
    //点的间距
    private int mDotSpacing = 8;
    //底部颜色 默认透明
    private int mBottomColor = Color.TRANSPARENT;
    //banner的底部的view
    private View mBannerBottomView;
    //banner的宽高比
    private float mWidthProportion,mHeightProportion;


    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        //布局填充
        inflate(context, R.layout.layout_banner, this);
        //获取属性
        initAttribute(context, attrs);
        //初始化view
        intiView();

    }

    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
        //获得点的位置
        mDotGravity = array.getInt(R.styleable.BannerView_dotGravity, mDotGravity);
        //获得指示点的颜色 选中和普通状态两种
        mIndicatorSelectedDrawable = array.getDrawable(R.styleable.BannerView_dotIndicatorSelectedColor);
        if (mIndicatorSelectedDrawable == null) {
            //如果在布局中没有配置点的默认颜色，用户没有设置点的资源，就默认设置一个
            mIndicatorSelectedDrawable = new ColorDrawable(Color.RED);
        }
        mIndicatorNormalDrawable = array.getDrawable(R.styleable.BannerView_dotIndicatorNormalColor);
        if (mIndicatorNormalDrawable == null) {
            mIndicatorNormalDrawable = new ColorDrawable(Color.GRAY);
        }
        //点的大小
        mDotSize = (int) array.getDimension(R.styleable.BannerView_dotSize, dip2px(mDotSize));
        //点的间距
        mDotSpacing = (int) array.getDimension(R.styleable.BannerView_dotSpacing, dip2px(mDotSpacing));
        //banner底部布局颜色，默认是透明
        mBottomColor = array.getColor(R.styleable.BannerView_bottomColor,mBottomColor);
        //获取banner的宽高比，用来做适配
        mWidthProportion = array.getFloat(R.styleable.BannerView_widthProportion,mWidthProportion);
        mHeightProportion = array.getFloat(R.styleable.BannerView_heightProportion,mHeightProportion);
        array.recycle();
    }

    /**
     * 初始化view
     */
    private void intiView() {
        mBannerVp = (BannerViewPager) findViewById(R.id.banner_vp);
        mBannerBottomView = findViewById(R.id.banner_bottom_view);
        mTextBannerDesc = (TextView) findViewById(R.id.tv_banner_desc);
        mDotContainerView = (LinearLayout) findViewById(R.id.ll_dot_container);
        mBannerBottomView.setBackgroundColor(mBottomColor);
    }


    /**
     * 设置适配器
     *
     * @param adapter
     */
    public void setAdapter(BannerAdapter adapter) {
        mBannerAdapter = adapter;
        mBannerVp.setAdapter(adapter);
        //初始化点的指示器
        initDotContainer();
        //广告切换的一个监听
        mBannerVp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //监听当前选中的位置
                pageSelected(position);
            }
        });
        //初始化的时候，获取第一条描述
        String firstDesc = mBannerAdapter.getBannerDesc(0);
        mTextBannerDesc.setText(firstDesc);
        //动态指定高度
        if (mHeightProportion == 0 ||mWidthProportion == 0){
            return;
        }
        //动态指定宽高
        int width = getMeasuredWidth();
        //计算高度
        int height = (int) ((width*mHeightProportion)/mWidthProportion);
        //指定宽高
        getLayoutParams().height = height;
        mBannerVp.getLayoutParams().height = height;
    }

    /**
     * 切面切换的回调
     *
     * @param position
     */
    private void pageSelected(int position) {
        //之前选中的点设置为默认
        DotIndicatorView oldIndicatorView = (DotIndicatorView) mDotContainerView.getChildAt(mCurrentPosition);
        oldIndicatorView.setDrawable(mIndicatorNormalDrawable);
        //把当前的位置点亮
        mCurrentPosition = position % mBannerAdapter.getCount();
        DotIndicatorView currentIndicatorView = (DotIndicatorView) mDotContainerView.getChildAt(mCurrentPosition);
        currentIndicatorView.setDrawable(mIndicatorSelectedDrawable);
        //广告位的描述
        String bannerDesc = mBannerAdapter.getBannerDesc(mCurrentPosition);
        mTextBannerDesc.setText(bannerDesc);

    }


    /**
     * 初始化点的指示器
     */
    private void initDotContainer() {
        int count = mBannerAdapter.getCount();
        //让点的位置在右边
        mDotContainerView.setGravity(getDotGravity());
        mDotContainerView.removeAllViews();
        for (int i = 0; i < count; i++) {
            //不断地往点的指示器里面添加圆点
            DotIndicatorView indicatorView = new DotIndicatorView(mContext);
            //设置大小
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mDotSize, mDotSize);
            params.leftMargin = mDotSpacing;
            indicatorView.setLayoutParams(params);
            //设置颜色
            if (i == 0) {
                indicatorView.setDrawable(mIndicatorSelectedDrawable);
            } else {
                indicatorView.setDrawable(mIndicatorNormalDrawable);
            }

            //将指示器添加进去
            mDotContainerView.addView(indicatorView);
        }

    }

    /**
     * 获取点的位置
     *
     * @return
     */
    private int getDotGravity() {
        switch (mDotGravity) {
            case 0:
                return Gravity.CENTER;
            case -1:
                return Gravity.LEFT;
            case 1:
                return Gravity.RIGHT;

        }
        return Gravity.RIGHT;
    }

    /**
     * 把dip转换为px
     *
     * @param dip
     * @return
     */
    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

    /**
     * 开始滚动
     */
    public void startRoll() {
        mBannerVp.startRoll();
    }

    /**
     * 隐藏页面指示器
     */
    public void hidePageIndicator() {
        mDotContainerView.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示页面指示器
     */
    public void showPageIndicator() {
        mDotContainerView.setVisibility(View.VISIBLE);
    }

    /**
     * 设置点击回调监听
     */
    public void setOnBannerItemClickListener(BannerViewPager.BannerItemClickListener listener) {
        mBannerVp.setOnBannerItemClickListener(listener);
    }
}
