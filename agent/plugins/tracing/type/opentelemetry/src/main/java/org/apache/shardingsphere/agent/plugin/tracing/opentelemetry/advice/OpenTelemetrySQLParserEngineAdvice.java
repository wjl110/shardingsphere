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

package org.apache.shardingsphere.agent.plugin.tracing.opentelemetry.advice;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import org.apache.shardingsphere.agent.api.advice.TargetAdviceObject;
import org.apache.shardingsphere.agent.plugin.tracing.core.advice.TracingSQLParserEngineAdvice;
import org.apache.shardingsphere.agent.plugin.tracing.opentelemetry.constant.OpenTelemetryConstants;

import java.lang.reflect.Method;

/**
 * OpenTelemetry SQL parser engine advice executor.
 */
public final class OpenTelemetrySQLParserEngineAdvice extends TracingSQLParserEngineAdvice<Span> {
    
    @Override
    protected Object recordSQLParseInfo(final Span rootSpan, final TargetAdviceObject target, final String sql) {
        Tracer tracer = GlobalOpenTelemetry.getTracer("shardingsphere-agent");
        SpanBuilder spanBuilder = tracer.spanBuilder(OPERATION_NAME)
                .setAttribute(OpenTelemetryConstants.COMPONENT, OpenTelemetryConstants.COMPONENT_NAME)
                .setAttribute(OpenTelemetryConstants.DB_TYPE, OpenTelemetryConstants.DB_TYPE_VALUE)
                .setAttribute(OpenTelemetryConstants.DB_STATEMENT, sql);
        if (null != rootSpan) {
            spanBuilder.setParent(Context.current().with(rootSpan));
        }
        return spanBuilder.startSpan();
    }
    
    @Override
    public void afterMethod(final TargetAdviceObject target, final Method method, final Object[] args, final Object result, final String pluginType) {
        ((Span) target.getAttachment()).end();
    }
    
    @Override
    public void onThrowing(final TargetAdviceObject target, final Method method, final Object[] args, final Throwable throwable, final String pluginType) {
        ((Span) target.getAttachment()).setStatus(StatusCode.ERROR).recordException(throwable);
    }
}
