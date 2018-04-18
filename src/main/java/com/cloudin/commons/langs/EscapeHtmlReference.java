package com.cloudin.commons.langs;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.util.RuntimeServicesAware;
import org.apache.velocity.util.StringUtils;

/**
 * 防xss攻击-转换html内容。"对所有不符合的做转换"
 * 
 */
public class EscapeHtmlReference implements ReferenceInsertionEventHandler, RuntimeServicesAware {

    private PatternCompiler compile = new Perl5Compiler();

    private Pattern         pattern = null;

    private RuntimeServices rs;

    /**
     * Escape the given text. Override this in a subclass to do the actual escaping.
     * 
     * @param text the text to escape
     * @return the escaped text
     */
    protected String escape(Object text) {
		return StringEscapeUtil.escapeHtml(text.toString());
    }

    /**
     * Specify the configuration attribute that specifies the regular expression. Ideally should be in a form
     * 
     * <pre>
     * <code>eventhandler.escape.XYZ.nomatch</code>
     * </pre>
     * <p>
     * where <code>XYZ</code> is not the type of escaping being done.
     * 
     * @return configuration attribute
     */
    protected String getMatchAttribute() {
        return "eventhandler.escape.html.nomatch";
    }

    /**
     * Escape the provided text if it matches the configured regular expression.
     * 
     * @param reference
     * @param value
     * @return Escaped text.
     */
    @Override
	public Object referenceInsert(String reference, Object value) {
        if (value == null) {
            return value;
        }

        if (pattern == null) {
            return escape(value);
        } else {
            PatternMatcher matcher = new Perl5Matcher();
            String s = null;
            if (reference.startsWith("$!{")) {
                s = reference.substring(3);
            } else if (reference.startsWith("$!")) {
                s = reference.substring(2);
            } else if (reference.startsWith("${")) {
                s = reference.substring(2);
            } else if (reference.startsWith("$")) {
                s = reference.substring(1);
            } else {
                s = reference;
            }
            if (matcher.contains(s, pattern)) {
                return value;
            } else {
                return escape(value);
            }
        }
    }

    /**
     * Called automatically when event cartridge is initialized.
     * 
     * @param rs instance of RuntimeServices
     */
    @Override
	public void setRuntimeServices(RuntimeServices rs) {
        this.rs = rs;

        /**
         * Get the regular expression pattern.
         */
        String matchRegExp = StringUtils.nullTrim(rs.getConfiguration().getString(getMatchAttribute()));
        if ((matchRegExp != null) && (matchRegExp.length() == 0)) {
            matchRegExp = null;
        }

        /**
         * Test the regular expression for a well formed pattern
         */
        if (matchRegExp != null) {
            try {
                pattern = compile.compile(matchRegExp);
                PatternMatcher matcher = new Perl5Matcher();
                matcher.contains("", pattern);
            } catch (MalformedPatternException e) {
                rs.getLog().error("EscapeHtmlReference matchRegExp err.", e);
            }
        }

    }

    /**
     * Retrieve a reference to RuntimeServices. Use this for checking additional configuration properties.
     * 
     * @return The current runtime services object.
     */
    protected RuntimeServices getRuntimeServices() {
        return rs;
    }

}
