package top.afinit.engine;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.stereotype.Component;
import top.afinit.model.context.ProjectContext;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Component
public class CodeTemplateEngine {

    private final Configuration cfg;

    public CodeTemplateEngine() {

        cfg = new Configuration(Configuration.VERSION_2_3_32);

        // 模板路径（resources/template）
        cfg.setClassLoaderForTemplateLoading(
                this.getClass().getClassLoader(),
                "/template"
        );

        cfg.setDefaultEncoding("UTF-8");

        cfg.setTemplateExceptionHandler(
                TemplateExceptionHandler.RETHROW_HANDLER
        );
    }

    /**
     * 核心渲染方法
     */
    public void render(ProjectContext context, File projectDir) throws Exception {
        if (Boolean.TRUE.equals(context.getGenerateCrud())) {
            renderCommonFiles(context, projectDir);
        }

        for (var table : context.getTables()) {

            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("project", context);
            dataModel.put("table", table);

            // 1. Entity
            renderToFile("entity.ftl",
                    dataModel,
                    projectDir + "/src/main/java/"
                            + context.getBasePackage().replace(".", "/")
                            + "/domain/entity/"
                            + table.getClassName() + ".java"
            );

            // 2. Mapper
            renderToFile("mapper.ftl",
                    dataModel,
                    projectDir + "/src/main/java/"
                            + context.getBasePackage().replace(".", "/")
                            + "/mapper/"
                            + table.getClassName() + "Mapper.java"
            );

            // 3. Service
            renderToFile("service.ftl",
                    dataModel,
                    projectDir + "/src/main/java/"
                            + context.getBasePackage().replace(".", "/")
                            + "/service/"
                            + table.getClassName() + "Service.java"
            );

            // 4. ServiceImpl
            renderToFile("serviceImpl.ftl",
                    dataModel,
                    projectDir + "/src/main/java/"
                            + context.getBasePackage().replace(".", "/")
                            + "/service/impl/"
                            + table.getClassName() + "ServiceImpl.java"
            );

            // 5. Controller
            renderToFile("controller.ftl",
                    dataModel,
                    projectDir + "/src/main/java/"
                            + context.getBasePackage().replace(".", "/")
                            + "/controller/"
                            + table.getClassName() + "Controller.java"
            );

            if (Boolean.TRUE.equals(context.getGenerateCrud())) {
                // 6. DTO
                renderToFile("dto.ftl",
                        dataModel,
                        projectDir + "/src/main/java/"
                                + context.getBasePackage().replace(".", "/")
                                + "/domain/dto/"
                                + table.getClassName() + "DTO.java"
                );

                // 7. VO
                renderToFile("vo.ftl",
                        dataModel,
                        projectDir + "/src/main/java/"
                                + context.getBasePackage().replace(".", "/")
                                + "/domain/vo/"
                                + table.getClassName() + "VO.java"
                );

                // 8. Query
                renderToFile("query.ftl",
                        dataModel,
                        projectDir + "/src/main/java/"
                                + context.getBasePackage().replace(".", "/")
                                + "/query/"
                                + table.getClassName() + "Query.java"
                );
            }

        }
    }

    private void renderCommonFiles(ProjectContext context, File projectDir) throws Exception {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("project", context);

        String basePath = projectDir + "/src/main/java/" + context.getBasePackage().replace(".", "/");

        renderToFile("apiResponse.ftl", dataModel, basePath + "/common/result/ApiResponse.java");
        renderToFile("pageResponse.ftl", dataModel, basePath + "/common/result/PageResponse.java");
        renderToFile("globalExceptionHandler.ftl", dataModel, basePath + "/exception/GlobalExceptionHandler.java");
        renderToFile("mybatisPlusConfig.ftl", dataModel, basePath + "/config/MybatisPlusConfig.java");
    }

    /**
     * 写文件
     */
    private void renderToFile(String templateName,
                              Map<String, Object> dataModel,
                              String outputPath) throws Exception {

        Template template = cfg.getTemplate(templateName);

        File file = new File(outputPath);
        file.getParentFile().mkdirs();

        try (Writer writer = new FileWriter(file)) {
            template.process(dataModel, writer);
        }
    }
}
