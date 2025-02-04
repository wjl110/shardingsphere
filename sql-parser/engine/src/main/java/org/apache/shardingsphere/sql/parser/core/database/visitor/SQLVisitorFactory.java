/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.sql.parser.core.database.visitor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.apache.shardingsphere.infra.util.spi.type.typed.TypedSPIRegistry;
import org.apache.shardingsphere.sql.parser.exception.SQLParsingException;
import org.apache.shardingsphere.sql.parser.spi.SQLVisitorFacade;
import org.apache.shardingsphere.sql.parser.sql.common.statement.SQLStatementType;

import java.util.Properties;

/**
 * SQL visitor factory.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SQLVisitorFactory {
    
    /**
     * Create new instance of SQL visitor.
     * 
     * @param databaseType database type
     * @param visitorType SQL visitor type
     * @param visitorRule SQL visitor rule
     * @param props SQL visitor config
     * @param <T> type of visitor result
     * @return created instance
     */
    public static <T> ParseTreeVisitor<T> newInstance(final String databaseType, final String visitorType, final SQLVisitorRule visitorRule, final Properties props) {
        SQLVisitorFacade facade = TypedSPIRegistry.getService(SQLVisitorFacade.class, String.join(".", databaseType, visitorType));
        return createParseTreeVisitor(facade, visitorRule.getType(), props);
    }
    
    @SuppressWarnings("unchecked")
    @SneakyThrows(ReflectiveOperationException.class)
    private static <T> ParseTreeVisitor<T> createParseTreeVisitor(final SQLVisitorFacade visitorFacade, final SQLStatementType type, final Properties props) {
        switch (type) {
            case DML:
                return (ParseTreeVisitor<T>) visitorFacade.getDMLVisitorClass().getConstructor(Properties.class).newInstance(props);
            case DDL:
                return (ParseTreeVisitor<T>) visitorFacade.getDDLVisitorClass().getConstructor(Properties.class).newInstance(props);
            case TCL:
                return (ParseTreeVisitor<T>) visitorFacade.getTCLVisitorClass().getConstructor(Properties.class).newInstance(props);
            case DCL:
                return (ParseTreeVisitor<T>) visitorFacade.getDCLVisitorClass().getConstructor(Properties.class).newInstance(props);
            case DAL:
                return (ParseTreeVisitor<T>) visitorFacade.getDALVisitorClass().getConstructor(Properties.class).newInstance(props);
            case RL:
                return (ParseTreeVisitor<T>) visitorFacade.getRLVisitorClass().getConstructor(Properties.class).newInstance(props);
            default:
                throw new SQLParsingException(type.name());
        }
    }
}
