/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.connector

import org.apache.spark.sql.AnalysisException

class AlterTablePartitionV2SQLSuite extends DatasourceV2SQLBase {
  test("ALTER TABLE RECOVER PARTITIONS") {
    val t = "testcat.ns1.ns2.tbl"
    withTable(t) {
      spark.sql(s"CREATE TABLE $t (id bigint, data string) USING foo")
      val e = intercept[AnalysisException] {
        sql(s"ALTER TABLE $t RECOVER PARTITIONS")
      }
      assert(e.message.contains(
        "ALTER TABLE ... RECOVER PARTITIONS is not supported for v2 tables."))
    }
  }

  test("ALTER TABLE RENAME PARTITION") {
    val nonPartTbl = "testcat.ns1.ns2.tbl"
    val partTbl = "testpart.ns1.ns2.tbl"
    withTable(nonPartTbl, partTbl) {
      spark.sql(s"CREATE TABLE $nonPartTbl (id bigint, data string) USING foo PARTITIONED BY (id)")
      val e1 = intercept[AnalysisException] {
        sql(s"ALTER TABLE $nonPartTbl PARTITION (id=1) RENAME TO PARTITION (id=2)")
      }
      assert(e1.message.contains(s"Table $nonPartTbl can not alter partitions"))

      spark.sql(s"CREATE TABLE $partTbl (id bigint, data string) USING foo PARTITIONED BY (id)")
      val e2 = intercept[AnalysisException] {
        sql(s"ALTER TABLE $partTbl PARTITION (id=1) RENAME TO PARTITION (id=2)")
      }
      assert(e2.message.contains(
        "ALTER TABLE ... RENAME TO PARTITION is not supported for v2 tables."))
    }
  }
}
