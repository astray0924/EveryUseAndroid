package org.everyuse.android.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ErrorHelper {
	public static String getMostProminentError(String response)
			throws JSONException {
		return getMostProminentError(new JSONObject(response));
	}

	public static String getMostProminentError(JSONObject response)
			throws JSONException {
		Map<String, List<String>> error_map = parseErrorResponse(response);
		String error = "";

		for (String field : error_map.keySet()) {
			List<String> error_msg_list = error_map.get(field);

			for (String err_msg : error_msg_list) {
				error = field + " " + err_msg;
				break;
			}
		}

		return error;
	}

	public static Map<String, List<String>> parseErrorResponse(String response)
			throws JSONException {
		JSONObject json = new JSONObject(response);
		return parseErrorResponse(json);
	}

	public static Map<String, List<String>> parseErrorResponse(
			JSONObject response) throws JSONException {
		Map<String, List<String>> errors = new HashMap<String, List<String>>();
		JSONArray error_names = response.names();

		for (int i = 0; i < error_names.length(); i++) {
			String error_name = error_names.getString(i);

			// generate List from JSONArray
			JSONArray error_contents = response.getJSONArray(error_name);
			List<String> error_msgs = new ArrayList<String>();

			for (int j = 0; j < error_contents.length(); j++) {
				String error_msg = error_contents.getString(j);
				error_msgs.add(error_msg);
			}

			errors.put(error_name, error_msgs);
		}

		return errors;
	}

}
