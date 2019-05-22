
package com.mycompany.jsoneventpaser;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Stack;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;

import static javax.json.stream.JsonParser.Event.VALUE_NULL;
import static javax.json.stream.JsonParser.Event.VALUE_NUMBER;

/**
 *
 * @author Mark
 */
public class JSONCrusher {

    public static String crush(String json) {

        JsonParser parser = Json.createParser(new StringReader(json));

        Stack<String> nameStack = new Stack<>();

        String currentKey = null;
        boolean inArray = false;

        Writer sw = new StringWriter();
        JsonGenerator generator = Json.createGenerator(sw);

        generator.writeStartObject();

        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();

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
                case KEY_NAME:
                    currentKey = parser.getString();
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
                    generator.write(buildKey(nameStack, currentKey), false);
                    break;
                case VALUE_NULL:
                    generator.writeNull(buildKey(nameStack, currentKey));
                    break;
                case VALUE_TRUE:
                    generator.write(buildKey(nameStack, currentKey), true);
                    break;
                case VALUE_NUMBER:
                    generator.write(buildKey(nameStack, currentKey), parser.getLong());
                    break;
                case VALUE_STRING:
                    if (inArray) {
                        // in array we need to get value value as the key
                        if ("value".equalsIgnoreCase(currentKey)) {
                            generator.write(buildKey(nameStack, parser.getString()), "true");
                        }
                    } else {
                        generator.write(buildKey(nameStack, currentKey), parser.getString());
                    }
            }
        }
        generator.writeEnd().close();
        return sw.toString();
    }

    private static String buildKey(Stack<String> path, String name) {
        if (path.empty()) {
            return name;
        } else {
            //cammal case path & name, max 30 chars
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
