# commons-langs
通用工具类

# fastjson

## BigDecimal 自定义序列化
### java 方式配置
全局初始化：
```java
// 以下代码，默认对 BigDecimal 保留 2 位小数
SerializeConfig.getGlobalInstance().put(BigDecimal.class, new DefaultBigDecimalSerializer());
SerializeConfig.getGlobalInstance().put(BigDecimal.class, new DefaultBigDecimalSerializer("0.00"));
SerializeConfig.getGlobalInstance().put(BigDecimal.class, new DefaultBigDecimalSerializer(new DecimalFormat("0.00")));

// 以下代码，默认对 BigDecimal 保留 4 位小数
SerializeConfig.getGlobalInstance().put(BigDecimal.class, new DefaultBigDecimalSerializer("0.0000"));
SerializeConfig.getGlobalInstance().put(BigDecimal.class, new DefaultBigDecimalSerializer(new DecimalFormat("0.0000")));
```
通过 `JSONField` 方式指定序列化方式：
```java
class DemoObject{
    @JSONField(format = "0.00")
    private BigDecimal price;
}
```

### spring mvc 配置

```xml

<mvc:annotation-driven>
    <mvc:message-converters>
        <bean class="org.springframework.http.converter.StringHttpMessageConverter">
            <constructor-arg name="defaultCharset" value="UTF-8" />
        </bean>
        <bean class="com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter">
            <property name="fastJsonConfig" ref="mvcFastJsonConfig" />
            <property name="supportedMediaTypes">
                <list>
                    <value>text/html;charset=UTF-8</value>
                    <value>application/json;charset=UTF-8</value>
                </list>
            </property>
        </bean>
    </mvc:message-converters>
</mvc:annotation-driven>

<bean id="mvcFastJsonConfig" class="com.alibaba.fastjson.support.config.FastJsonConfig">
    <property name="charset" value="UTF-8" />
    <property name="dateFormat" value="yyyy-MM-dd HH:mm:ss" />
    <property name="serializerFeatures">
        <array>
            <!-- 关闭循环引用检测，这是fastjson的特性，不是json标准里的，禁用 -->
            <value>DisableCircularReferenceDetect</value>
        </array>
    </property>
    <property name="serializeConfig">
        <bean class="com.cloudin.commons.langs.support.fastjson.SerializeConfigFactoryBean">
            <!-- 禁用 asm 特性，asm 特性 bug 挺多，禁用后可以解决泛型里，自定义序列化不起作用的问题 -->
            <property name="asm" value="false" />
            <property name="serializer">
                <map key-type="java.lang.String" value-type="com.alibaba.fastjson.serializer.ObjectSerializer">
                    <!-- 添加 BigDecimal 自定义序列化类 -->
                    <entry key="java.math.BigDecimal">
                        <bean class="com.cloudin.commons.langs.support.fastjson.DefaultBigDecimalSerializer">
                            <!-- BigDecimal 默认输出两位小数 -->
                            <constructor-arg name="defaultFormat" value="0.00" />
                        </bean>
                    </entry>
                </map>
            </property>
        </bean>
    </property>
</bean>

```
