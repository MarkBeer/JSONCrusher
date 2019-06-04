package com.mycompany.jsoneventpaser;

//import static javax.json.stream.JsonParser.Event.VALUE_NULL;
//import static javax.json.stream.JsonParser.Event.VALUE_NUMBER;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Stack;

import com.fasterxml.jackson.core.JsonFactory;

//import javax.json.Json;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import java.io.IOException;

/**
 *
 * @author Mark
 */
public class JsonCrusherFasterXMLJackson {

    public static String crush(String json) throws IOException {
        Writer sw = new StringWriter();
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(new StringReader(json));

            JsonParser parser = new TreeTraversingParser(jsonNode);// Json.createParser(new StringReader(json));

            Stack<String> nameStack = new Stack<>();

            String currentKey = null;
            boolean inArray = false;

            JsonFactory factory = new JsonFactory();
            JsonGenerator generator = factory.createGenerator(sw);

            generator.writeStartObject();

            JsonToken event = parser.nextToken();
            while (event != null) {
                switch (event) {
                    case START_OBJECT:
                        if (currentKey != null && !inArray) {
                            nameStack.push(currentKey);
                        }
                        break;
                    case END_OBJECT:
                        if (!nameStack.empty() && !inArray) {
                            nameStack.pop();
                        }
                        break;
                    case FIELD_NAME:
                        currentKey = parser.getText();//String();
                        break;
                    case START_ARRAY:
                        inArray = true;
                        nameStack.push(currentKey);
                        break;
                    case END_ARRAY:
                        inArray = false;
                        nameStack.pop();
                        break;
                    case VALUE_FALSE:
                        generator.writeBooleanField(buildKey(nameStack, currentKey), false);
                        break;
                    case VALUE_NULL:
                        generator.writeNullField(buildKey(nameStack, currentKey));
                        break;
                    case VALUE_TRUE:
                        generator.writeBooleanField(buildKey(nameStack, currentKey), true);
                        break;
                    case VALUE_NUMBER_INT:
                        generator.writeNumberField(buildKey(nameStack, currentKey), parser.getIntValue());
                        break;
                    case VALUE_NUMBER_FLOAT:
                        generator.writeNumberField(buildKey(nameStack, currentKey), parser.getLongValue());
                        break;
                    case VALUE_STRING:
                        if (inArray) {
                            // in array we need to get value value as the key
                            if ("value".equalsIgnoreCase(currentKey)) {
                                generator.writeStringField(buildKey(nameStack, parser.getText()), "true");
                            }
                        } else {
                            generator.writeStringField(buildKey(nameStack, currentKey), parser.getText());
                        }
                    default:
                        break;
                }

                event = parser.nextToken();
            }
            generator.writeEndObject();
            generator.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sw.toString();
    }

    private static String buildKey(Stack<String> path, String name) {
        if (path.empty()) {
            return name;
        } else {
            //camel case path & name, max 30 chars
            StringBuilder sb = new StringBuilder();
            path.forEach((element) -> {
                sb.append(element.substring(0, 1).toUpperCase());
                sb.append(element.substring(1, 2));
            });
            sb.append(name.substring(0, 1).toUpperCase());
            sb.append(name.substring(1));
            sb.replace(0, 1, sb.substring(0, 1).toLowerCase());
            return sb.toString().substring(0, sb.length() > 30 ? 30 : sb.length());
        }
    }

}
