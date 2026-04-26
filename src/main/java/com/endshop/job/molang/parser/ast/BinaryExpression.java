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
 * Represents a binary expression, such as {@code a + b} or {@code a == b}.
 *
 * @since 3.0.0
 */
public class BinaryExpression implements Expression {
    private final Expression left;
    private final Operator operator;
    private final Expression right;

    public BinaryExpression(@NotNull Expression left, @NotNull Operator operator, @NotNull Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public @NotNull Expression left() {
        return left;
    }

    public @NotNull Operator operator() {
        return operator;
    }

    public @NotNull Expression right() {
        return right;
    }

    public enum Operator {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO, POWER,
        EQUAL, NOT_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL,
        AND, OR, XOR
    }
}