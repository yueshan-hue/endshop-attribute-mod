/*
 * This file is part of molang, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.endshop.job.molang.parser.ast;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a for expression, such as {@code for (variable in iterable) body}.
 *
 * @since 3.0.0
 */
public class ForExpression implements Expression {
    private final String variable;
    private final Expression iterable;
    private final Expression body;

    public ForExpression(@NotNull String variable, @NotNull Expression iterable, @NotNull Expression body) {
        this.variable = variable;
        this.iterable = iterable;
        this.body = body;
    }

    public @NotNull String variable() {
        return variable;
    }

    public @NotNull Expression iterable() {
        return iterable;
    }

    public @NotNull Expression body() {
        return body;
    }
}