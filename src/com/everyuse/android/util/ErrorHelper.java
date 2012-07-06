package com.everyuse.android.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ErrorHelper {
	public static String getMostProminentError(String response, String[] fields) throws JSONException {
		return getMostProminentError(new JSONObject(response), fields);
	}

	public static String getMostProminentError(JSONObject response, String[] fields) throws JSONException {
		Map<String, List<String>> error_map = parseErrorResponse(response);
		String prominent_error = null;

		for (int i = 0; i < fields.length; i++) {
			String field = fields[i];

			if (error_map.containsKey(field)) {
				prominent_error = field + " " + error_map.get(field).get(0);
				break;
			}
		}

		return prominent_error;
	}

	public static Map<String, List<String>> parseErrorResponse(String response) throws JSONException {
		JSONObject json = new JSONObject(response);
		return parseErrorResponse(json);
	}

	public static Map<String, List<String>> parseErrorResponse(JSONObject response) throws JSONException {
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
