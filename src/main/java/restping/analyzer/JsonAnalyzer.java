package restping.analyzer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Map;

public class JsonAnalyzer {

    // Analyze and print the tree structure
    public void printTree(JsonElement element, String indent) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();

                if (value.isJsonObject()) {
                    System.out.println(indent + " ├── " + key + " (object)");
                    printTree(value, indent + " │   ");

                } else if (value.isJsonArray()) {
                    JsonArray arr = value.getAsJsonArray();
                    System.out.println(indent + " ├── " + key + " (array, size: " + arr.size() + ")");
                    if (arr.size() > 0) {
                        printTree(arr.get(0), indent + " │   ");
                    }

                } else if (value.isJsonPrimitive()) {
                    String type = detectType(value.getAsJsonPrimitive());
                    System.out.println(indent + " ├── " + key + " → " + type);
                }
            }
        }
    }

    // Detect primitive type
    public String detectType(JsonPrimitive primitive) {
        if (primitive.isBoolean()) return "boolean";
        if (primitive.isNumber()) {
            double d = primitive.getAsDouble();
            if (d == Math.floor(d)) return "int";
            return "double";
        }
        return "String";
    }

    // Generate full Java class with nested inner classes
    public String generateClass(String className, JsonElement element) {
        StringBuilder sb = new StringBuilder();
        buildClass(className, element, sb, 0);
        return sb.toString();
    }

    // Recursive class builder
    private void buildClass(String className, JsonElement element, StringBuilder sb, int depth) {
        String indent = "    ".repeat(depth);
        String fieldIndent = "    ".repeat(depth + 1);

        if (depth == 0) {
            sb.append("public class ").append(capitalize(className)).append(" {\n");
        } else {
            sb.append(indent).append("static class ").append(capitalize(className)).append(" {\n");
        }

        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();

            // First pass — print all fields
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();

                if (value.isJsonObject()) {
                    sb.append(fieldIndent).append(capitalize(key)).append(" ").append(key).append(";\n");
                } else if (value.isJsonArray()) {
                    JsonArray arr = value.getAsJsonArray();
                    if (arr.size() > 0 && arr.get(0).isJsonObject()) {
                        sb.append(fieldIndent).append("List<").append(capitalize(key)).append("> ").append(key).append(";\n");
                    } else {
                        sb.append(fieldIndent).append("List<String> ").append(key).append(";\n");
                    }
                } else if (value.isJsonPrimitive()) {
                    String type = detectType(value.getAsJsonPrimitive());
                    sb.append(fieldIndent).append(type).append(" ").append(key).append(";\n");
                }
            }

            // Second pass — build nested inner classes
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();

                if (value.isJsonObject()) {
                    sb.append("\n");
                    buildClass(key, value, sb, depth + 1);
                } else if (value.isJsonArray()) {
                    JsonArray arr = value.getAsJsonArray();
                    if (arr.size() > 0 && arr.get(0).isJsonObject()) {
                        sb.append("\n");
                        buildClass(key, arr.get(0), sb, depth + 1);
                    }
                }
            }
        }

        sb.append(indent).append("}\n");
    }

    // Capitalize first letter
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}