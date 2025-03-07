/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.gateway.ha.persistence.dao;

import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface QueryHistoryDao
{
    @SqlQuery("""
            SELECT * FROM query_history
            ORDER BY created DESC
            LIMIT 2000
            """)
    List<QueryHistory> findRecentQueries();

    @SqlQuery("""
            SELECT * FROM query_history
            WHERE user_name = :userName
            ORDER BY created DESC
            LIMIT 2000
            """)
    List<QueryHistory> findRecentQueriesByUserName(String userName);

    @SqlQuery("""
            SELECT backend_url FROM query_history
            WHERE query_id = :queryId
            """)
    String findBackendUrlByQueryId(String queryId);

    @SqlUpdate("""
            INSERT INTO query_history (query_id, query_text, backend_url, user_name, source, created)
            VALUES (:queryId, :queryText, :backendUrl, :userName, :source, :created)
            """)
    void insertHistory(String queryId, String queryText, String backendUrl, String userName, String source, long created);

    @SqlUpdate("""
            DELETE FROM query_history
            WHERE created < :created
            """)
    void deleteOldHistory(long created);
}
