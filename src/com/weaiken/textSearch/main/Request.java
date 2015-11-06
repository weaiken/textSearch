package com.weaiken.textSearch.main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weaiken.textSearch.util.JSONUtils;

public class Request {

	private final static String httpUrl = "http://apis.baidu.com/apistore/tranlateservice/translate";
	private final static String apiKey = "52bc04e2b3e9c88305b87bd7baa55997";

	private static ExecutorService mExecutors;

	static {
		mExecutors = Executors.newCachedThreadPool();
	}

	public void request(final String requestString, final Response response) {

		if (null == mExecutors)
			mExecutors = Executors.newCachedThreadPool();

		final String httpUrlRequst = httpUrl + "?from=en&to=zh&query=";
		mExecutors.execute(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				BufferedReader reader = null;
				String result = null;
				StringBuffer sbf = new StringBuffer();

				try {
					URL url = new URL(httpUrlRequst + requestString);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					// 填入apikey到HTTP header
					connection.setRequestProperty("apikey", apiKey);
					connection.connect();
					InputStream is = connection.getInputStream();
					reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					String strRead = null;
					while ((strRead = reader.readLine()) != null) {
						sbf.append(strRead);
						sbf.append("\r\n");
					}
					reader.close();
					result = sbf.toString();

					DataModel mDataModel = spliteData(result);
					if (mDataModel.getCode() == 0) {
						response.success(mDataModel.getResult());
					} else {
						response.fail(mDataModel.getCode(), mDataModel.getResult());
					}

				} catch (Exception e) {
					e.printStackTrace();
					response.fail(400, e.getMessage());
				}
			}
		});
	}

	public DataModel spliteData(String result) {

		int code = JSONUtils.getInt(result, "errNum", 400);
		String errMsg = JSONUtils.getString(result, "errMsg", "fail");
		if (code != 0) {
			return new DataModel(code, errMsg);
		} else {
			JSONObject mJsonObject = JSONUtils.getJSONObject(result, "retData", null);
			if (null != mJsonObject) {
				try {
					JSONArray mJsonArray = JSONUtils.getJSONArray(mJsonObject, "trans_result", null);
					if (null != mJsonArray) {
						JSONObject nJsonObject = mJsonArray.getJSONObject(0);
						String translateString = JSONUtils.getString(nJsonObject, "dst", "null");
						return new DataModel(0,translateString);
					} else {

						return new DataModel(100, "fail");

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				return new DataModel(300, "解包Fail");
			}
		}
		return new DataModel(200, errMsg);
	}

	// {
	// "errNum": 0,
	// "errMsg": "success",
	// "retData": {
	// "from": "en",
	// "to": "zh",
	// "trans_result": [
	// {
	// "src": "I am chinese,and you?",
	// "dst": "我是中国人，你呢？"
	// }
	// ]
	// }
	// }

	public class DataModel {
		int code;
		String result;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}

		public DataModel(int code, String result) {
			super();
			this.code = code;
			this.result = result;
		}

		public DataModel() {
			super();
		}

	}

	public interface Response {
		void success(String msg);

		void fail(int errorNum, String msg);
	}

}
