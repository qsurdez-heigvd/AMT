package ddl;

import ch.heig.amt.vineward.business.model.Canton;
import ch.heig.amt.vineward.business.model.GrapeType;
import ch.heig.amt.vineward.business.model.Region;
import ch.heig.amt.vineward.business.model.Wine;
import ch.heig.amt.vineward.business.model.WineVariety;
import ch.heig.amt.vineward.business.model.review.Comment;
import ch.heig.amt.vineward.business.model.review.Review;
import ch.heig.amt.vineward.business.model.review.ReviewStatus;
import ch.heig.amt.vineward.business.model.user.User;
import ch.heig.amt.vineward.business.model.user.UserRole;
import ch.heig.amt.vineward.business.model.user.UserToken;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.junit.jupiter.api.Test;

public class DDLGenerator {

    @Test
    void generateDDL() {
        var metadata = new MetadataSources(
            new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.temp.use_jdbc_metadata_defaults", false)
                .build()
        ).addAnnotatedClasses(
            Comment.class,
            Review.class,
            User.class,
            UserToken.class,
            Wine.class,
            ReviewStatus.class,
            UserRole.class,
            Canton.class,
            GrapeType.class,
            Region.class,
            WineVariety.class
        ).buildMetadata();

        deleteFile("sql/ddl/create.sql");
        deleteFile("sql/ddl/drop.sql");

        var schemaExport = new SchemaExport();
        schemaExport.setFormat(true);
        schemaExport.setOutputFile("sql/ddl/create.sql");
        schemaExport.createOnly(EnumSet.of(TargetType.SCRIPT), metadata);
        schemaExport.setOutputFile("sql/ddl/drop.sql");
        schemaExport.drop(EnumSet.of(TargetType.SCRIPT), metadata);
    }

    private void deleteFile(String path) {
        try {
            Files.delete(Path.of(path));
        } catch (Exception ignored) {
        }
    }
}
