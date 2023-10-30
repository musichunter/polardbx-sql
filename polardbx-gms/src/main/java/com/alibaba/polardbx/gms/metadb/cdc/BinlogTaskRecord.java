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

package com.alibaba.polardbx.gms.metadb.cdc;

import com.alibaba.fastjson.JSON;
import com.alibaba.polardbx.gms.metadb.record.SystemTableRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author yudong
 * @since 2023/6/9 15:02
 **/
public class BinlogTaskRecord implements SystemTableRecord {
    private String ip;
    private int port;
    private String role;
    private List<String> sourcesList;

    @SuppressWarnings("unchecked")
    @Override
    public BinlogTaskRecord fill(ResultSet rs) throws SQLException {
        this.ip = rs.getString(1);
        this.port = rs.getInt(2);
        this.role = rs.getString(3);
        this.sourcesList = JSON.parseObject(rs.getString(4), List.class);
        return this;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getRole() {
        return role;
    }

    public List<String> getSourcesList() {
        return sourcesList;
    }

}
