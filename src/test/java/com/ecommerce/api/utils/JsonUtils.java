package com.ecommerce.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Utility class for JSON manipulation and validation
 * Provides methods for parsing, validating, and extracting data from JSON responses
 */
public class JsonUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Parse JSON string to JsonNode
     * @param jsonString JSON string to parse
     * @return JsonNode representation of the JSON
     * @throws RuntimeException if JSON parsing fails
     */
    public static JsonNode parseJson(String jsonString) {
        try {
            if (jsonString == null || jsonString.trim().isEmpty()) {
                throw new IllegalArgumentException("JSON string cannot be null or empty");
            }
            return objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON string: {}", jsonString, e);
            throw new RuntimeException("Invalid JSON format: " + e.getMessage(), e);
        }
    }
    
    /**
     * Convert object to JSON string
     * @param object Object to convert
     * @return JSON string representation
     * @throws RuntimeException if serialization fails
     */
    public static String toJsonString(Object object) {
        try {
            if (object == null) {
                return "null";
            }
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize object to JSON: {}", object, e);
            throw new RuntimeException("Failed to serialize object to JSON: " + e.getMessage(), e);
        }
    }
    
    /**
     * Convert object to pretty-printed JSON string
     * @param object Object to convert
     * @return Pretty-printed JSON string
     * @throws RuntimeException if serialization fails
     */
    public static String toPrettyJsonString(Object object) {
        try {
            if (object == null) {
                return "null";
            }
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize object to pretty JSON: {}", object, e);
            throw new RuntimeException("Failed to serialize object to pretty JSON: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extract value from JSON using JSONPath-like syntax
     * @param jsonString JSON string
     * @param path Path to the value (e.g., "user.name", "products[0].id")
     * @return Extracted value as string, or null if path not found
     */
    public static String extractValue(String jsonString, String path) {
        try {
            JsonNode rootNode = parseJson(jsonString);
            JsonNode valueNode = navigateToPath(rootNode, path);
            
            if (valueNode == null || valueNode.isNull()) {
                return null;
            }
            
            return valueNode.isTextual() ? valueNode.asText() : valueNode.toString();
        } catch (Exception e) {
            logger.warn("Failed to extract value at path '{}' from JSON: {}", path, e.getMessage());
            return null;
        }
    }
    
    /**
     * Extract value from JSON and convert to specified type
     * @param jsonString JSON string
     * @param path Path to the value
     * @param targetClass Target class for conversion
     * @param <T> Type of the target class
     * @return Extracted and converted value, or null if not found or conversion fails
     */
    @SuppressWarnings("unchecked")
    public static <T> T extractValue(String jsonString, String path, Class<T> targetClass) {
        try {
            JsonNode rootNode = parseJson(jsonString);
            JsonNode valueNode = navigateToPath(rootNode, path);
            
            if (valueNode == null || valueNode.isNull()) {
                return null;
            }
            
            if (targetClass == String.class) {
                return (T) (valueNode.isTextual() ? valueNode.asText() : valueNode.toString());
            } else if (targetClass == Integer.class) {
                return (T) Integer.valueOf(valueNode.asInt());
            } else if (targetClass == Long.class) {
                return (T) Long.valueOf(valueNode.asLong());
            } else if (targetClass == Double.class) {
                return (T) Double.valueOf(valueNode.asDouble());
            } else if (targetClass == Boolean.class) {
                return (T) Boolean.valueOf(valueNode.asBoolean());
            } else {
                return objectMapper.treeToValue(valueNode, targetClass);
            }
        } catch (Exception e) {
            logger.warn("Failed to extract and convert value at path '{}' to {}: {}", 
                       path, targetClass.getSimpleName(), e.getMessage());
            return null;
        }
    }
    
    /**
     * Check if JSON contains a specific path
     * @param jsonString JSON string
     * @param path Path to check
     * @return true if path exists, false otherwise
     */
    public static boolean hasPath(String jsonString, String path) {
        try {
            JsonNode rootNode = parseJson(jsonString);
            JsonNode valueNode = navigateToPath(rootNode, path);
            return valueNode != null && !valueNode.isNull();
        } catch (Exception e) {
            logger.warn("Failed to check path '{}' in JSON: {}", path, e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate if string is valid JSON
     * @param jsonString String to validate
     * @return true if valid JSON, false otherwise
     */
    public static boolean isValidJson(String jsonString) {
        try {
            parseJson(jsonString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get all keys from a JSON object
     * @param jsonString JSON string
     * @return List of keys, empty list if not a JSON object
     */
    public static List<String> getKeys(String jsonString) {
        try {
            JsonNode rootNode = parseJson(jsonString);
            if (!rootNode.isObject()) {
                return new ArrayList<>();
            }
            
            List<String> keys = new ArrayList<>();
            Iterator<String> fieldNames = rootNode.fieldNames();
            while (fieldNames.hasNext()) {
                keys.add(fieldNames.next());
            }
            return keys;
        } catch (Exception e) {
            logger.warn("Failed to get keys from JSON: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get size of JSON array or object
     * @param jsonString JSON string
     * @return Size of array/object, -1 if not array or object
     */
    public static int getSize(String jsonString) {
        try {
            JsonNode rootNode = parseJson(jsonString);
            if (rootNode.isArray() || rootNode.isObject()) {
                return rootNode.size();
            }
            return -1;
        } catch (Exception e) {
            logger.warn("Failed to get size of JSON: {}", e.getMessage());
            return -1;
        }
    }
    
    /**
     * Compare two JSON strings for equality (ignoring order)
     * @param json1 First JSON string
     * @param json2 Second JSON string
     * @return true if JSONs are equal, false otherwise
     */
    public static boolean areEqual(String json1, String json2) {
        try {
            JsonNode node1 = parseJson(json1);
            JsonNode node2 = parseJson(json2);
            return node1.equals(node2);
        } catch (Exception e) {
            logger.warn("Failed to compare JSONs: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Merge two JSON objects
     * @param baseJson Base JSON object
     * @param updateJson JSON object with updates
     * @return Merged JSON string
     * @throws RuntimeException if merge fails
     */
    public static String mergeJsonObjects(String baseJson, String updateJson) {
        try {
            JsonNode baseNode = parseJson(baseJson);
            JsonNode updateNode = parseJson(updateJson);
            
            if (!baseNode.isObject() || !updateNode.isObject()) {
                throw new IllegalArgumentException("Both JSONs must be objects for merging");
            }
            
            ObjectNode mergedNode = (ObjectNode) baseNode.deepCopy();
            Iterator<Map.Entry<String, JsonNode>> fields = updateNode.fields();
            
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                mergedNode.set(field.getKey(), field.getValue());
            }
            
            return toJsonString(mergedNode);
        } catch (Exception e) {
            logger.error("Failed to merge JSON objects", e);
            throw new RuntimeException("Failed to merge JSON objects: " + e.getMessage(), e);
        }
    }
    
    /**
     * Navigate to a specific path in JSON node
     * @param rootNode Root JSON node
     * @param path Path to navigate (e.g., "user.name", "products[0].id")
     * @return JsonNode at the path, or null if not found
     */
    private static JsonNode navigateToPath(JsonNode rootNode, String path) {
        if (path == null || path.trim().isEmpty()) {
            return rootNode;
        }
        
        JsonNode currentNode = rootNode;
        String[] pathParts = path.split("\\.");
        
        for (String part : pathParts) {
            if (currentNode == null) {
                return null;
            }
            
            // Handle array access like "products[0]"
            if (part.contains("[") && part.contains("]")) {
                String fieldName = part.substring(0, part.indexOf("["));
                String indexStr = part.substring(part.indexOf("[") + 1, part.indexOf("]"));
                
                try {
                    int index = Integer.parseInt(indexStr);
                    currentNode = currentNode.get(fieldName);
                    
                    if (currentNode != null && currentNode.isArray() && index < currentNode.size()) {
                        currentNode = currentNode.get(index);
                    } else {
                        return null;
                    }
                } catch (NumberFormatException e) {
                    return null;
                }
            } else {
                currentNode = currentNode.get(part);
            }
        }
        
        return currentNode;
    }
    
    /**
     * Validate JSON schema structure
     * @param jsonString JSON to validate
     * @param requiredFields List of required field paths
     * @return true if all required fields are present, false otherwise
     */
    public static boolean validateSchema(String jsonString, List<String> requiredFields) {
        try {
            for (String field : requiredFields) {
                if (!hasPath(jsonString, field)) {
                    logger.warn("Required field '{}' not found in JSON", field);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.warn("Failed to validate JSON schema: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract all values from JSON array
     * @param jsonString JSON array string
     * @param fieldPath Path to field in each array element
     * @return List of extracted values
     */
    public static List<String> extractArrayValues(String jsonString, String fieldPath) {
        List<String> values = new ArrayList<>();
        try {
            JsonNode rootNode = parseJson(jsonString);
            if (!rootNode.isArray()) {
                logger.warn("JSON is not an array");
                return values;
            }
            
            for (JsonNode element : rootNode) {
                JsonNode valueNode = navigateToPath(element, fieldPath);
                if (valueNode != null && !valueNode.isNull()) {
                    values.add(valueNode.isTextual() ? valueNode.asText() : valueNode.toString());
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to extract array values: {}", e.getMessage());
        }
        return values;
    }
}