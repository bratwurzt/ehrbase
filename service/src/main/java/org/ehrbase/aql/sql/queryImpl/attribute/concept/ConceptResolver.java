/*
 * Copyright (c) 2019 Vitasystems GmbH and Christian Chevalley (Hannover Medical School).
 *
 * This file is part of project EHRbase
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehrbase.aql.sql.queryImpl.attribute.concept;

import org.ehrbase.aql.sql.queryImpl.QueryImplConstants;
import org.ehrbase.aql.sql.queryImpl.attribute.AttributeResolver;
import org.ehrbase.aql.sql.queryImpl.attribute.FieldResolutionContext;
import org.ehrbase.aql.sql.queryImpl.attribute.JoinSetup;
import org.ehrbase.aql.sql.queryImpl.attribute.eventcontext.EventContextJson;
import org.jooq.Field;
import org.jooq.TableField;

import java.util.Arrays;

import static org.ehrbase.jooq.pg.tables.EventContext.EVENT_CONTEXT;

public class ConceptResolver extends AttributeResolver
{

    TableField tableField;

    public ConceptResolver(FieldResolutionContext fieldResolutionContext, JoinSetup joinSetup) {
        super(fieldResolutionContext, joinSetup);
    }

    public Field<?> sqlField(String path){

        if (path.isEmpty())
            return new ConceptJson(fieldResolutionContext, joinSetup).forTableField(tableField).sqlField();

        if (!path.equals("mappings") && path.startsWith("mappings")) {
            path = path.substring(path.indexOf("mappings")+"mappings".length()+1);
            //we insert a tag to indicate that the path operates on a json array
            return new ConceptJson(fieldResolutionContext, joinSetup).forJsonPath("mappings/"+ QueryImplConstants.AQL_NODE_ITERATIVE_MARKER+"/" + path).forTableField(tableField).sqlField();
        }
        else if (Arrays.asList("value", "mappings", "defining_code", "defining_code/terminology_id", "defining_code/terminology_id/value", "defining_code/code_string").contains(path)) {
            Field sqlField = new ConceptJson(fieldResolutionContext, joinSetup).forJsonPath(path).forTableField(tableField).sqlField();
            if (path.equals("defining_code/terminology_id/value")||path.equals("value"))
                fieldResolutionContext.setJsonDatablock(false);
            return sqlField;
        }
        else
            throw new IllegalArgumentException("Unresolved concept attribute path:"+path);
    }

    public ConceptResolver forTableField(TableField tableField) {
        this.tableField = tableField;
        return this;
    }
}
