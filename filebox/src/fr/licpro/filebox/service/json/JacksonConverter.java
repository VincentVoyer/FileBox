package fr.licpro.filebox.service.json;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;


/**
 * Converter from String to Jackson object
 */
public class JacksonConverter implements Converter {
	
	/**
	 * Object mapper
	 */
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


	@Override
	public Object fromBody(TypedInput body, Type type) throws ConversionException {
		JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructType(type);
		
		try {
			return OBJECT_MAPPER.readValue(body.in(), javaType);
		}
		catch (IOException e) {
			throw new ConversionException(e);
		}
	}

	@Override
	public TypedOutput toBody(Object object) {
		try {
			String charset = "UTF-8";
			return new JsonTypedOutput(OBJECT_MAPPER.writeValueAsString(object).getBytes(charset), charset);
		}
		catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	private static class JsonTypedOutput implements TypedOutput {
		private final byte[] jsonBytes;
		private final String mimeType;
		
		public JsonTypedOutput(byte[] jsonBytes, String charset) {
			this.jsonBytes = jsonBytes;
			this.mimeType = "application/json; charset=" + charset;
		}

		@Override
		public String fileName() {
			return null;
		}

		@Override
		public String mimeType() {
			return mimeType;
		}

		@Override
		public long length() {
			return jsonBytes.length;
		}

		@Override
		public void writeTo(OutputStream out) throws IOException {
			out.write(jsonBytes);
		}
	}
}
