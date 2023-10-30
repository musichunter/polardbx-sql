/*
 * Copyright [2013-2021], Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: DumperServer.proto

package com.alibaba.polardbx.rpc.cdc;

public interface MasterStatusOrBuilder extends
    // @@protoc_insertion_point(interface_extends:dumper.MasterStatus)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string file = 1;</code>
   * @return The file.
   */
  java.lang.String getFile();
  /**
   * <code>string file = 1;</code>
   * @return The bytes for file.
   */
  com.google.protobuf.ByteString
      getFileBytes();

  /**
   * <code>int64 position = 2;</code>
   * @return The position.
   */
  long getPosition();

  /**
   * <code>string binlogDoDB = 3;</code>
   * @return The binlogDoDB.
   */
  java.lang.String getBinlogDoDB();
  /**
   * <code>string binlogDoDB = 3;</code>
   * @return The bytes for binlogDoDB.
   */
  com.google.protobuf.ByteString
      getBinlogDoDBBytes();

  /**
   * <code>string binlogIgnoreDB = 4;</code>
   * @return The binlogIgnoreDB.
   */
  java.lang.String getBinlogIgnoreDB();
  /**
   * <code>string binlogIgnoreDB = 4;</code>
   * @return The bytes for binlogIgnoreDB.
   */
  com.google.protobuf.ByteString
      getBinlogIgnoreDBBytes();

  /**
   * <code>string executedGtidSet = 5;</code>
   * @return The executedGtidSet.
   */
  java.lang.String getExecutedGtidSet();
  /**
   * <code>string executedGtidSet = 5;</code>
   * @return The bytes for executedGtidSet.
   */
  com.google.protobuf.ByteString
      getExecutedGtidSetBytes();
}
