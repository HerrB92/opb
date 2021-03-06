/*
 *  Copyright 2010, 2011 Christopher Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package obp.persistence.joda;

import java.sql.Timestamp;
import java.sql.Types;

import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.TimestampType;

public abstract class AbstractTimestampColumnMapper<T> extends AbstractColumnMapper<T, Timestamp> implements ColumnMapper<T, Timestamp> {

    private static final long serialVersionUID = -3070239764121234482L;

    @Override
    public final TimestampType getHibernateType() {
    	return StandardBasicTypes.TIMESTAMP;
    }

    @Override
    public final int getSqlType() {
        return Types.TIMESTAMP;
    }

    @Override
    public abstract T fromNonNullValue(Timestamp value);

    @Override
    public abstract T fromNonNullString(String s);

    @Override
    public abstract Timestamp toNonNullValue(T value);

    @Override
    public abstract String toNonNullString(T value);
}
