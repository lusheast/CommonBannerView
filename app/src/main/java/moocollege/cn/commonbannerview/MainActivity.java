package moocollege.cn.commonbannerview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import moocollege.cn.commonbannerview.banner.BannerView;
import moocollege.cn.commonbannerview.banner.BannerAdapter;
import moocollege.cn.commonbannerview.banner.RequestData;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private BannerView mBannerView;
    private OkHttpClient mOkHttpClient;
    private static Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBannerView = (BannerView) findViewById(R.id.banner_view);
        mOkHttpClient = new OkHttpClient();
        initBannerData();
    }

    private void initBannerData() {
        Request.Builder builder = new Request.Builder();
        String url = "http://zjedu2.api.moocollege.com/teaching_app/banner/obtain?code=SITES_BANNER&tenantId=2";
        builder.url(url);
        mOkHttpClient.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                RequestData requestData = new Gson().fromJson(result, RequestData.class);
                final List<RequestData.DataBean> datalist = requestData.getData();
                //该方法不是在主线程中
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showBannerData(datalist);
                    }
                });

            }
        });
    }

    private void showBannerData(final List<RequestData.DataBean> list) {
        final List<RequestData.DataBean> datalist = new ArrayList<>();
        RequestData.DataBean bean1 = new RequestData.DataBean("http://img5.caijing.com.cn/2014/0902/1409631031356.jpg","0","聆听");
        RequestData.DataBean bean2 = new RequestData.DataBean("http://photocdn.sohu.com/20150803/mp25514367_1438583601670_4.jpeg","1","蒋方舟");
        RequestData.DataBean bean3 = new RequestData.DataBean("http://imglf0.ph.126.net/Ti_qPbnHhO9fKZXLyjnAuw==/6597810837494774880.jpg","2","完美爱情");
        datalist.add(bean1);
        datalist.add(bean2);
        datalist.add(bean3);
        mBannerView.setAdapter(new BannerAdapter() {
            @Override
            public View getView(int position,View convertView) {
                ImageView bannerIV = null;
                if (convertView == null){
                  bannerIV = new ImageView(MainActivity.this);
                }else{
                    bannerIV = (ImageView) convertView;
                }
                bannerIV.setAdjustViewBounds(true);
                bannerIV.setScaleType(ImageView.ScaleType.CENTER_CROP);//  调整图片大小
                android.view.ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                bannerIV.setLayoutParams(params);
                String image_url = datalist.get(position).getImageUrl();
                Glide.with(MainActivity.this).load(image_url).placeholder(R.drawable.iv_course_default).into(bannerIV);
                return bannerIV;
            }

            @Override
            public int getCount() {
                return datalist.size();
            }

            /**
             * 广告位不是必须的 有就覆盖该方法 没有就不覆盖
             * @param mCurrentPosition
             * @return
             */
            @Override
            public String getBannerDesc(int mCurrentPosition) {
                return datalist.get(mCurrentPosition).getTitle();
            }
        });
        mBannerView.startRoll();//开启滚动
    }
}
