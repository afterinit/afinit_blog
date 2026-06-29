package top.afinit.builder;

import org.springframework.stereotype.Component;
import top.afinit.model.request.ProjectRequest;
import top.afinit.util.FileUtil;

import java.io.File;

@Component
public class ProjectBuilder {

    public File build(ProjectRequest request) {
        String basePath = System.getProperty("java.io.tmpdir")
                + File.separator + request.getProjectName();

        File root = new File(basePath);
        if (root.exists()) {
            FileUtil.deleteRecursively(root);
        }
        root.mkdirs();

        String packagePath = request.getBasePackage().replace(".", "/");

        new File(root, "src/main/java/" + packagePath + "/common/result").mkdirs();
        new File(root, "src/main/java/" + packagePath + "/config").mkdirs();
        new File(root, "src/main/java/" + packagePath + "/controller").mkdirs();
        new File(root, "src/main/java/" + packagePath + "/domain/dto").mkdirs();
        new File(root, "src/main/java/" + packagePath + "/domain/entity").mkdirs();
        new File(root, "src/main/java/" + packagePath + "/domain/vo").mkdirs();
        new File(root, "src/main/java/" + packagePath + "/service").mkdirs();
        new File(root, "src/main/java/" + packagePath + "/service/impl").mkdirs();
        new File(root, "src/main/java/" + packagePath + "/mapper").mkdirs();
        new File(root, "src/main/java/" + packagePath + "/exception").mkdirs();
        new File(root, "src/main/java/" + packagePath + "/query").mkdirs();

        new File(root, "src/main/resources").mkdirs();

        return root;
    }
}
