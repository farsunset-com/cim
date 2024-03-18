// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ReplyBody.proto

package com.farsunset.cim.sdk.android.model.proto;

public final class ReplyBodyProto {
  private ReplyBodyProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }
  public interface ReplyModelOrBuilder extends
      // @@protoc_insertion_point(interface_extends:com.farsunset.cim.sdk.android.model.proto.ReplyModel)
      com.google.protobuf.MessageLiteOrBuilder {

    /**
     * <code>string key = 1;</code>
     * @return The key.
     */
    String getKey();
    /**
     * <code>string key = 1;</code>
     * @return The bytes for key.
     */
    com.google.protobuf.ByteString
        getKeyBytes();

    /**
     * <code>string code = 2;</code>
     * @return The code.
     */
    String getCode();
    /**
     * <code>string code = 2;</code>
     * @return The bytes for code.
     */
    com.google.protobuf.ByteString
        getCodeBytes();

    /**
     * <code>string message = 3;</code>
     * @return The message.
     */
    String getMessage();
    /**
     * <code>string message = 3;</code>
     * @return The bytes for message.
     */
    com.google.protobuf.ByteString
        getMessageBytes();

    /**
     * <code>int64 timestamp = 4;</code>
     * @return The timestamp.
     */
    long getTimestamp();

    /**
     * <code>map&lt;string, string&gt; data = 5;</code>
     */
    int getDataCount();
    /**
     * <code>map&lt;string, string&gt; data = 5;</code>
     */
    boolean containsData(
        String key);
    /**
     * Use {@link #getDataMap()} instead.
     */
    @Deprecated
    java.util.Map<String, String>
    getData();
    /**
     * <code>map&lt;string, string&gt; data = 5;</code>
     */
    java.util.Map<String, String>
    getDataMap();
    /**
     * <code>map&lt;string, string&gt; data = 5;</code>
     */

    /* nullable */
String getDataOrDefault(
        String key,
        /* nullable */
String defaultValue);
    /**
     * <code>map&lt;string, string&gt; data = 5;</code>
     */

    String getDataOrThrow(
        String key);
  }
  /**
   * Protobuf type {@code com.farsunset.cim.sdk.android.model.proto.ReplyModel}
   */
  public  static final class ReplyModel extends
      com.google.protobuf.GeneratedMessageLite<
          ReplyModel, ReplyModel.Builder> implements
      // @@protoc_insertion_point(message_implements:com.farsunset.cim.sdk.android.model.proto.ReplyModel)
      ReplyModelOrBuilder {
    private ReplyModel() {
      key_ = "";
      code_ = "";
      message_ = "";
    }
    public static final int KEY_FIELD_NUMBER = 1;
    private String key_;
    /**
     * <code>string key = 1;</code>
     * @return The key.
     */
    @Override
    public String getKey() {
      return key_;
    }
    /**
     * <code>string key = 1;</code>
     * @return The bytes for key.
     */
    @Override
    public com.google.protobuf.ByteString
        getKeyBytes() {
      return com.google.protobuf.ByteString.copyFromUtf8(key_);
    }
    /**
     * <code>string key = 1;</code>
     * @param value The key to set.
     */
    private void setKey(
        String value) {
      Class<?> valueClass = value.getClass();
  
      key_ = value;
    }
    /**
     * <code>string key = 1;</code>
     */
    private void clearKey() {
      
      key_ = getDefaultInstance().getKey();
    }
    /**
     * <code>string key = 1;</code>
     * @param value The bytes for key to set.
     */
    private void setKeyBytes(
        com.google.protobuf.ByteString value) {
      checkByteStringIsUtf8(value);
      key_ = value.toStringUtf8();
      
    }

    public static final int CODE_FIELD_NUMBER = 2;
    private String code_;
    /**
     * <code>string code = 2;</code>
     * @return The code.
     */
    @Override
    public String getCode() {
      return code_;
    }
    /**
     * <code>string code = 2;</code>
     * @return The bytes for code.
     */
    @Override
    public com.google.protobuf.ByteString
        getCodeBytes() {
      return com.google.protobuf.ByteString.copyFromUtf8(code_);
    }
    /**
     * <code>string code = 2;</code>
     * @param value The code to set.
     */
    private void setCode(
        String value) {
      Class<?> valueClass = value.getClass();
  
      code_ = value;
    }
    /**
     * <code>string code = 2;</code>
     */
    private void clearCode() {
      
      code_ = getDefaultInstance().getCode();
    }
    /**
     * <code>string code = 2;</code>
     * @param value The bytes for code to set.
     */
    private void setCodeBytes(
        com.google.protobuf.ByteString value) {
      checkByteStringIsUtf8(value);
      code_ = value.toStringUtf8();
      
    }

    public static final int MESSAGE_FIELD_NUMBER = 3;
    private String message_;
    /**
     * <code>string message = 3;</code>
     * @return The message.
     */
    @Override
    public String getMessage() {
      return message_;
    }
    /**
     * <code>string message = 3;</code>
     * @return The bytes for message.
     */
    @Override
    public com.google.protobuf.ByteString
        getMessageBytes() {
      return com.google.protobuf.ByteString.copyFromUtf8(message_);
    }
    /**
     * <code>string message = 3;</code>
     * @param value The message to set.
     */
    private void setMessage(
        String value) {
      Class<?> valueClass = value.getClass();
  
      message_ = value;
    }
    /**
     * <code>string message = 3;</code>
     */
    private void clearMessage() {
      
      message_ = getDefaultInstance().getMessage();
    }
    /**
     * <code>string message = 3;</code>
     * @param value The bytes for message to set.
     */
    private void setMessageBytes(
        com.google.protobuf.ByteString value) {
      checkByteStringIsUtf8(value);
      message_ = value.toStringUtf8();
      
    }

    public static final int TIMESTAMP_FIELD_NUMBER = 4;
    private long timestamp_;
    /**
     * <code>int64 timestamp = 4;</code>
     * @return The timestamp.
     */
    @Override
    public long getTimestamp() {
      return timestamp_;
    }
    /**
     * <code>int64 timestamp = 4;</code>
     * @param value The timestamp to set.
     */
    private void setTimestamp(long value) {
      
      timestamp_ = value;
    }
    /**
     * <code>int64 timestamp = 4;</code>
     */
    private void clearTimestamp() {
      
      timestamp_ = 0L;
    }

    public static final int DATA_FIELD_NUMBER = 5;
    private static final class DataDefaultEntryHolder {
      static final com.google.protobuf.MapEntryLite<
          String, String> defaultEntry =
              com.google.protobuf.MapEntryLite
              .<String, String>newDefaultInstance(
                  com.google.protobuf.WireFormat.FieldType.STRING,
                  "",
                  com.google.protobuf.WireFormat.FieldType.STRING,
                  "");
    }
    private com.google.protobuf.MapFieldLite<
        String, String> data_ =
            com.google.protobuf.MapFieldLite.emptyMapField();
    private com.google.protobuf.MapFieldLite<String, String>
    internalGetData() {
      return data_;
    }
    private com.google.protobuf.MapFieldLite<String, String>
    internalGetMutableData() {
      if (!data_.isMutable()) {
        data_ = data_.mutableCopy();
      }
      return data_;
    }
    @Override

    public int getDataCount() {
      return internalGetData().size();
    }
    /**
     * <code>map&lt;string, string&gt; data = 5;</code>
     */
    @Override

    public boolean containsData(
        String key) {
      Class<?> keyClass = key.getClass();
      return internalGetData().containsKey(key);
    }
    /**
     * Use {@link #getDataMap()} instead.
     */
    @Override
    @Deprecated
    public java.util.Map<String, String> getData() {
      return getDataMap();
    }
    /**
     * <code>map&lt;string, string&gt; data = 5;</code>
     */
    @Override

    public java.util.Map<String, String> getDataMap() {
      return java.util.Collections.unmodifiableMap(
          internalGetData());
    }
    /**
     * <code>map&lt;string, string&gt; data = 5;</code>
     */
    @Override

    public String getDataOrDefault(
        String key,
        String defaultValue) {
      Class<?> keyClass = key.getClass();
      java.util.Map<String, String> map =
          internalGetData();
      return map.containsKey(key) ? map.get(key) : defaultValue;
    }
    /**
     * <code>map&lt;string, string&gt; data = 5;</code>
     */
    @Override

    public String getDataOrThrow(
        String key) {
      Class<?> keyClass = key.getClass();
      java.util.Map<String, String> map =
          internalGetData();
      if (!map.containsKey(key)) {
        throw new IllegalArgumentException();
      }
      return map.get(key);
    }
    /**
     * <code>map&lt;string, string&gt; data = 5;</code>
     */
    private java.util.Map<String, String>
    getMutableDataMap() {
      return internalGetMutableData();
    }

    public static ReplyModel parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static ReplyModel parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static ReplyModel parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static ReplyModel parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static ReplyModel parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static ReplyModel parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static ReplyModel parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input);
    }
    public static ReplyModel parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input, extensionRegistry);
    }
    public static ReplyModel parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return parseDelimitedFrom(DEFAULT_INSTANCE, input);
    }
    public static ReplyModel parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
    }
    public static ReplyModel parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input);
    }
    public static ReplyModel parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input, extensionRegistry);
    }

    public static Builder newBuilder() {
      return (Builder) DEFAULT_INSTANCE.createBuilder();
    }
    public static Builder newBuilder(ReplyModel prototype) {
      return (Builder) DEFAULT_INSTANCE.createBuilder(prototype);
    }

    /**
     * Protobuf type {@code com.farsunset.cim.sdk.android.model.proto.ReplyModel}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageLite.Builder<
          ReplyModel, Builder> implements
        // @@protoc_insertion_point(builder_implements:com.farsunset.cim.sdk.android.model.proto.ReplyModel)
        ReplyModelOrBuilder {
      // Construct using com.farsunset.cim.sdk.android.model.proto.ReplyBodyProto.ReplyModel.newBuilder()
      private Builder() {
        super(DEFAULT_INSTANCE);
      }


      /**
       * <code>string key = 1;</code>
       * @return The key.
       */
      @Override
      public String getKey() {
        return instance.getKey();
      }
      /**
       * <code>string key = 1;</code>
       * @return The bytes for key.
       */
      @Override
      public com.google.protobuf.ByteString
          getKeyBytes() {
        return instance.getKeyBytes();
      }
      /**
       * <code>string key = 1;</code>
       * @param value The key to set.
       * @return This builder for chaining.
       */
      public Builder setKey(
          String value) {
        copyOnWrite();
        instance.setKey(value);
        return this;
      }
      /**
       * <code>string key = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearKey() {
        copyOnWrite();
        instance.clearKey();
        return this;
      }
      /**
       * <code>string key = 1;</code>
       * @param value The bytes for key to set.
       * @return This builder for chaining.
       */
      public Builder setKeyBytes(
          com.google.protobuf.ByteString value) {
        copyOnWrite();
        instance.setKeyBytes(value);
        return this;
      }

      /**
       * <code>string code = 2;</code>
       * @return The code.
       */
      @Override
      public String getCode() {
        return instance.getCode();
      }
      /**
       * <code>string code = 2;</code>
       * @return The bytes for code.
       */
      @Override
      public com.google.protobuf.ByteString
          getCodeBytes() {
        return instance.getCodeBytes();
      }
      /**
       * <code>string code = 2;</code>
       * @param value The code to set.
       * @return This builder for chaining.
       */
      public Builder setCode(
          String value) {
        copyOnWrite();
        instance.setCode(value);
        return this;
      }
      /**
       * <code>string code = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearCode() {
        copyOnWrite();
        instance.clearCode();
        return this;
      }
      /**
       * <code>string code = 2;</code>
       * @param value The bytes for code to set.
       * @return This builder for chaining.
       */
      public Builder setCodeBytes(
          com.google.protobuf.ByteString value) {
        copyOnWrite();
        instance.setCodeBytes(value);
        return this;
      }

      /**
       * <code>string message = 3;</code>
       * @return The message.
       */
      @Override
      public String getMessage() {
        return instance.getMessage();
      }
      /**
       * <code>string message = 3;</code>
       * @return The bytes for message.
       */
      @Override
      public com.google.protobuf.ByteString
          getMessageBytes() {
        return instance.getMessageBytes();
      }
      /**
       * <code>string message = 3;</code>
       * @param value The message to set.
       * @return This builder for chaining.
       */
      public Builder setMessage(
          String value) {
        copyOnWrite();
        instance.setMessage(value);
        return this;
      }
      /**
       * <code>string message = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearMessage() {
        copyOnWrite();
        instance.clearMessage();
        return this;
      }
      /**
       * <code>string message = 3;</code>
       * @param value The bytes for message to set.
       * @return This builder for chaining.
       */
      public Builder setMessageBytes(
          com.google.protobuf.ByteString value) {
        copyOnWrite();
        instance.setMessageBytes(value);
        return this;
      }

      /**
       * <code>int64 timestamp = 4;</code>
       * @return The timestamp.
       */
      @Override
      public long getTimestamp() {
        return instance.getTimestamp();
      }
      /**
       * <code>int64 timestamp = 4;</code>
       * @param value The timestamp to set.
       * @return This builder for chaining.
       */
      public Builder setTimestamp(long value) {
        copyOnWrite();
        instance.setTimestamp(value);
        return this;
      }
      /**
       * <code>int64 timestamp = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearTimestamp() {
        copyOnWrite();
        instance.clearTimestamp();
        return this;
      }

      @Override

      public int getDataCount() {
        return instance.getDataMap().size();
      }
      /**
       * <code>map&lt;string, string&gt; data = 5;</code>
       */
      @Override

      public boolean containsData(
          String key) {
        Class<?> keyClass = key.getClass();
        return instance.getDataMap().containsKey(key);
      }

      public Builder clearData() {
        copyOnWrite();
        instance.getMutableDataMap().clear();
        return this;
      }
      /**
       * <code>map&lt;string, string&gt; data = 5;</code>
       */

      public Builder removeData(
          String key) {
        Class<?> keyClass = key.getClass();
        copyOnWrite();
        instance.getMutableDataMap().remove(key);
        return this;
      }
      /**
       * Use {@link #getDataMap()} instead.
       */
      @Override
      @Deprecated
      public java.util.Map<String, String> getData() {
        return getDataMap();
      }
      /**
       * <code>map&lt;string, string&gt; data = 5;</code>
       */
      @Override
      public java.util.Map<String, String> getDataMap() {
        return java.util.Collections.unmodifiableMap(
            instance.getDataMap());
      }
      /**
       * <code>map&lt;string, string&gt; data = 5;</code>
       */
      @Override

      public String getDataOrDefault(
          String key,
          String defaultValue) {
        Class<?> keyClass = key.getClass();
        java.util.Map<String, String> map =
            instance.getDataMap();
        return map.containsKey(key) ? map.get(key) : defaultValue;
      }
      /**
       * <code>map&lt;string, string&gt; data = 5;</code>
       */
      @Override

      public String getDataOrThrow(
          String key) {
        Class<?> keyClass = key.getClass();
        java.util.Map<String, String> map =
            instance.getDataMap();
        if (!map.containsKey(key)) {
          throw new IllegalArgumentException();
        }
        return map.get(key);
      }
      /**
       * <code>map&lt;string, string&gt; data = 5;</code>
       */
      public Builder putData(
          String key,
          String value) {
        Class<?> keyClass = key.getClass();
        Class<?> valueClass = value.getClass();
        copyOnWrite();
        instance.getMutableDataMap().put(key, value);
        return this;
      }
      /**
       * <code>map&lt;string, string&gt; data = 5;</code>
       */
      public Builder putAllData(
          java.util.Map<String, String> values) {
        copyOnWrite();
        instance.getMutableDataMap().putAll(values);
        return this;
      }

      // @@protoc_insertion_point(builder_scope:com.farsunset.cim.sdk.android.model.proto.ReplyModel)
    }
    @Override
    @SuppressWarnings({"unchecked", "fallthrough"})
    protected final Object dynamicMethod(
        MethodToInvoke method,
        Object arg0, Object arg1) {
      switch (method) {
        case NEW_MUTABLE_INSTANCE: {
          return new ReplyModel();
        }
        case NEW_BUILDER: {
          return new Builder();
        }
        case BUILD_MESSAGE_INFO: {
            Object[] objects = new Object[] {
              "key_",
              "code_",
              "message_",
              "timestamp_",
              "data_",
              DataDefaultEntryHolder.defaultEntry,
            };
            String info =
                "\u0000\u0005\u0000\u0000\u0001\u0005\u0005\u0001\u0000\u0000\u0001\u0208\u0002\u0208" +
                "\u0003\u0208\u0004\u0002\u00052";
            return newMessageInfo(DEFAULT_INSTANCE, info, objects);
        }
        // fall through
        case GET_DEFAULT_INSTANCE: {
          return DEFAULT_INSTANCE;
        }
        case GET_PARSER: {
          com.google.protobuf.Parser<ReplyModel> parser = PARSER;
          if (parser == null) {
            synchronized (ReplyModel.class) {
              parser = PARSER;
              if (parser == null) {
                parser =
                    new DefaultInstanceBasedParser<ReplyModel>(
                        DEFAULT_INSTANCE);
                PARSER = parser;
              }
            }
          }
          return parser;
      }
      case GET_MEMOIZED_IS_INITIALIZED: {
        return (byte) 1;
      }
      case SET_MEMOIZED_IS_INITIALIZED: {
        return null;
      }
      }
      throw new UnsupportedOperationException();
    }


    // @@protoc_insertion_point(class_scope:com.farsunset.cim.sdk.android.model.proto.ReplyModel)
    private static final ReplyModel DEFAULT_INSTANCE;
    static {
      ReplyModel defaultInstance = new ReplyModel();
      // New instances are implicitly immutable so no need to make
      // immutable.
      DEFAULT_INSTANCE = defaultInstance;
      com.google.protobuf.GeneratedMessageLite.registerDefaultInstance(
        ReplyModel.class, defaultInstance);
    }

    public static ReplyModel getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static volatile com.google.protobuf.Parser<ReplyModel> PARSER;

    public static com.google.protobuf.Parser<ReplyModel> parser() {
      return DEFAULT_INSTANCE.getParserForType();
    }
  }


  static {
  }

  // @@protoc_insertion_point(outer_class_scope)
}