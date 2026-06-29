package top.afinit.service.Impl;

import org.springframework.stereotype.Service;
import top.afinit.builder.ProjectBuilder;
import top.afinit.converter.MpIdTypeConverter;
import top.afinit.converter.NameConverter;
import top.afinit.converter.TypeConverter;
import top.afinit.engine.CodeTemplateEngine;
import top.afinit.model.context.ColumnContext;
import top.afinit.model.context.ProjectContext;
import top.afinit.model.context.TableContext;
import top.afinit.model.request.ProjectRequest;
import top.afinit.service.GenerateService;
import top.afinit.util.ZipUtil;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

@Service
public class GenerateServiceImpl implements GenerateService {

    private final ProjectBuilder projectBuilder;
    private final CodeTemplateEngine codeTemplateEngine;

    public GenerateServiceImpl(ProjectBuilder projectBuilder,
                               CodeTemplateEngine codeTemplateEngine) {
        this.projectBuilder = projectBuilder;
        this.codeTemplateEngine = codeTemplateEngine;
    }

    @Override
    public File generate(ProjectRequest request) throws Exception {
        // 1. 构建目录
        File projectDir = projectBuilder.build(request);

        // 2. 构建Context
        ProjectContext context = buildContext(request);

        // 3. 生成代码
        codeTemplateEngine.render(context, projectDir);

        // 4. 生成pom
        generatePom(context, projectDir);

        // 5. 生成yml
        generateYaml(context, projectDir);

        // 6. 生成启动类
        generateApplication(context, projectDir);

        // 7. zip
        return ZipUtil.zip(projectDir);
    }

    /**
     * Context转换（你第二步逻辑）
     */
    private ProjectContext buildContext(ProjectRequest request) {
        ProjectContext ctx = new ProjectContext();
        ctx.setProjectName(normalizeProjectName(request.getProjectName()));
        ctx.setDatabaseName(normalizeDatabaseName(request.getDatabaseName(), ctx.getProjectName()));
        ctx.setBasePackage(normalizeBasePackage(request.getBasePackage()));
        ctx.setGenerateCrud(Boolean.TRUE.equals(request.getGenerateCrud()));

        var tables = request.getTables().stream().map(t -> {

            var tc = new TableContext();

            tc.setTableName(t.getTableName());
            tc.setClassName(NameConverter.toClassName(t.getTableName()));
            tc.setComment(t.getComment());

            var cols = t.getColumns().stream().map(c -> {

                var cc = new ColumnContext();

                cc.setColumnName(c.getColumnName());
                cc.setJavaField(NameConverter.toCamel(c.getColumnName()));
                cc.setSqlType(c.getSqlType());
                cc.setJavaType(TypeConverter.toJavaType(c.getSqlType()));
                cc.setPrimary(c.getPrimary());
                cc.setNotNull(c.getNotNull());
                cc.setComment(c.getComment());
                cc.setTableFieldAnnotation(
                        !Objects.equals(c.getColumnName(), NameConverter.toCamel(c.getColumnName()))
                );
                cc.setValidationAnnotation(buildValidationAnnotation(cc));

                if (Boolean.TRUE.equals(c.getPrimary())) {
                    cc.setIdType(
                            MpIdTypeConverter
                                    .resolve(c.getSqlType(), true)
                    );
                }

                return cc;
            }).toList();

            tc.setColumns(cols);
            tc.setPrimaryColumn(
                    cols.stream().filter(c -> Boolean.TRUE.equals(c.getPrimary())).findFirst()
                            .orElse(cols.isEmpty() ? null : cols.getFirst())
            );
            tc.setImports(
                    cols.stream()
                            .map(ColumnContext::getJavaType)
                            .map(TypeConverter::toImport)
                            .filter(Objects::nonNull)
                            .distinct()
                            .toList()
            );
            tc.setEntityImports(buildEntityImports(tc));
            tc.setDtoImports(buildDtoImports(tc));
            tc.setQueryImports(buildQueryImports(tc));
            return tc;

        }).toList();

        ctx.setTables(tables);
        return ctx;
    }

    /**
     * pom.xml生成
     */
    private void generatePom(ProjectContext context, File dir) throws Exception {
        String artifactId = toArtifactId(context.getProjectName());
        String validationDependency = Boolean.TRUE.equals(context.getGenerateCrud())
                ? """
                        
                                <dependency>
                                    <groupId>org.springframework.boot</groupId>
                                    <artifactId>spring-boot-starter-validation</artifactId>
                                </dependency>"""
                : "";

        String content =
                """
                        <project xmlns="http://maven.apache.org/POM/4.0.0"
                                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                 xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                                 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                        
                            <parent>
                                <groupId>org.springframework.boot</groupId>
                                <artifactId>spring-boot-starter-parent</artifactId>
                                <version>3.5.3</version>
                                <relativePath/>
                            </parent>
                        
                            <modelVersion>4.0.0</modelVersion>
                        
                            <groupId>%s</groupId>
                            <artifactId>%s</artifactId>
                            <version>1.0.0</version>
                            <name>%s</name>
                        
                            <properties>
                                <java.version>21</java.version>
                            </properties>
                        
                            <dependencies>
                        
                                <!-- SpringBoot -->
                                <dependency>
                                    <groupId>org.springframework.boot</groupId>
                                    <artifactId>spring-boot-starter-web</artifactId>
                                </dependency>
                                %s
                        
                                <!-- MyBatis-Plus -->
                                <dependency>
                                    <groupId>com.baomidou</groupId>
                                    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
                                    <version>3.5.7</version>
                                </dependency>
                        
                                <!-- MySQL -->
                                <dependency>
                                    <groupId>com.mysql</groupId>
                                    <artifactId>mysql-connector-j</artifactId>
                                    <scope>runtime</scope>
                                </dependency>
                        
                                <!-- Lombok -->
                                <dependency>
                                    <groupId>org.projectlombok</groupId>
                                    <artifactId>lombok</artifactId>
                                    <optional>true</optional>
                                </dependency>
                        
                            </dependencies>

                            <build>
                                <plugins>
                                    <plugin>
                                        <groupId>org.springframework.boot</groupId>
                                        <artifactId>spring-boot-maven-plugin</artifactId>
                                    </plugin>
                                </plugins>
                            </build>
                        
                        </project>
                        """.formatted(
                        context.getBasePackage(),
                        artifactId,
                        artifactId,
                        validationDependency
                );

        Files.writeString(
                new File(dir, "pom.xml").toPath(),
                content
        );
    }

    /**
     * application.yml生成
     */
    private void generateYaml(ProjectContext context, File dir) throws Exception {
        String appName = toArtifactId(context.getProjectName());
        String databaseName = context.getDatabaseName();

        String yaml =
                """
                        spring:
                          application:
                            name: %s
                          datasource:
                            url: jdbc:mysql://localhost:3306/%s?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8
                            username: root
                            password: root
                            driver-class-name: com.mysql.cj.jdbc.Driver
                        
                        server:
                          port: 8080
                        
                        mybatis-plus:
                          configuration:
                            log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
                        """.formatted(appName, databaseName);

        File file = new File(dir,
                "src/main/resources/application.yml");

        file.getParentFile().mkdirs();

        Files.writeString(file.toPath(), yaml);
    }

    /**
     * Spring Boot 启动类生成
     */
    private void generateApplication(ProjectContext context, File dir) throws Exception {
        String applicationClassName = NameConverter.toClassName(context.getProjectName()) + "Application";

        String content =
                """
                        package %s;
                        
                        import org.mybatis.spring.annotation.MapperScan;
                        import org.springframework.boot.SpringApplication;
                        import org.springframework.boot.autoconfigure.SpringBootApplication;
                        
                        @SpringBootApplication
                        @MapperScan("%s.mapper")
                        public class %s {
                        
                            public static void main(String[] args) {
                                SpringApplication.run(%s.class, args);
                            }
                        }
                        """.formatted(
                        context.getBasePackage(),
                        context.getBasePackage(),
                        applicationClassName,
                        applicationClassName
                );

        File file = new File(
                dir,
                "src/main/java/" + context.getBasePackage().replace(".", "/")
                        + "/" + applicationClassName + ".java"
        );

        file.getParentFile().mkdirs();
        Files.writeString(file.toPath(), content);
    }

    private String normalizeProjectName(String projectName) {
        String normalized = projectName == null ? "" : projectName.trim();
        return normalized.isEmpty() ? "generated-project" : normalized;
    }

    private String normalizeBasePackage(String basePackage) {
        String normalized = basePackage == null ? "" : basePackage.trim();
        return normalized.isEmpty() ? "com.generated" : normalized;
    }

    private String normalizeDatabaseName(String databaseName, String projectName) {
        String normalized = databaseName == null ? "" : databaseName.trim();
        if (normalized.isEmpty()) {
            return toArtifactId(projectName).replace('-', '_');
        }
        return normalized;
    }

    private String toArtifactId(String projectName) {
        return normalizeProjectName(projectName)
                .toLowerCase()
                .replaceAll("[^a-z0-9._-]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^[.-]+|[.-]+$", "");
    }

    private List<String> buildEntityImports(TableContext tableContext) {
        return mergeImports(
                tableContext.getImports(),
                tableContext.getColumns().stream()
                        .anyMatch(c -> Boolean.TRUE.equals(c.getTableFieldAnnotation()))
                        ? List.of("com.baomidou.mybatisplus.annotation.TableField")
                        : List.of()
        );
    }

    private List<String> buildDtoImports(TableContext tableContext) {
        return mergeImports(
                tableContext.getImports(),
                tableContext.getColumns().stream()
                        .map(ColumnContext::getValidationAnnotation)
                        .filter(Objects::nonNull)
                        .map(this::validationImportFor)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList()
        );
    }

    private List<String> buildQueryImports(TableContext tableContext) {
        return mergeImports(tableContext.getImports(), List.of());
    }

    private List<String> mergeImports(List<String> first, List<String> second) {
        return java.util.stream.Stream.concat(first.stream(), second.stream())
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private String buildValidationAnnotation(ColumnContext column) {
        if (!Boolean.TRUE.equals(column.getNotNull()) || Boolean.TRUE.equals(column.getPrimary())) {
            return null;
        }

        if ("String".equals(column.getJavaType())) {
            return "@NotBlank";
        }

        return "@NotNull";
    }

    private String validationImportFor(String annotation) {
        if (annotation == null) {
            return null;
        }

        return switch (annotation) {
            case "@NotBlank" -> "jakarta.validation.constraints.NotBlank";
            case "@NotNull" -> "jakarta.validation.constraints.NotNull";
            default -> null;
        };
    }
}
