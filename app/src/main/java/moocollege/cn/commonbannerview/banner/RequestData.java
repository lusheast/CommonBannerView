package moocollege.cn.commonbannerview.banner;

import java.util.List;

/**
 * Created by zsd on 2017/7/31 09:44
 * desc:
 */

public class RequestData {

    /**
     * code : 20000
     * message : success
     * data : [{"imageUrl":"http://image.moocollege.com/v2_customer/pJA1FJM3CmI8U8qExVPdrr0qbWYY1aemC3wTZYPqgJByzNMe3ZP28UDlVNrFkMN1_1499678490232.png","url":"#"},{"imageUrl":"http://image.moocollege.com/v2_customer/RCzQD9-WrRQVeQx3FnxIhuuOuV0rI5gmd8WKgKO2ElRPUW44x89Ui2o6z53B7Lj8_1499677938792.png","url":"#"},{"imageUrl":"http://image.moocollege.com/v2_customer/QCEGfO9oMtRYClMhv-gycLNNTTrbphJZO9M6apxSfeYcS1ge8y6JNgsaYhG4ji1q_1499678743859.png","url":"#"}]
     */

    private int code;
    private String message;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * imageUrl : http://image.moocollege.com/v2_customer/pJA1FJM3CmI8U8qExVPdrr0qbWYY1aemC3wTZYPqgJByzNMe3ZP28UDlVNrFkMN1_1499678490232.png
         * url : #
         */

        private String imageUrl;
        private String url;
        private String title;

        public DataBean(String imageUrl, String url, String title) {
            this.imageUrl = imageUrl;
            this.url = url;
            this.title = title;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
