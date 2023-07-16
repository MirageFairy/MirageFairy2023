plugins {
    id("fabric-loom") version "1.1-SNAPSHOT"
    id("maven-publish")
    kotlin("jvm") version "1.8.0"
}

version = project.property("mod_version") as String

loom {
    splitEnvironmentSourceSets()

    mods {
        register("modid") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.getByName("client"))
        }
    }
    runs {
        // これにより、datagen API を実行する新しい gradle タスク "gradlew runDatagen" が追加されます。
        register("datagen") {
            inherit(getByName("server"))
            name("Data Generation")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/main/generated")}")
            vmArg("-Dfabric-api.datagen.modid=${project.property("mod_id") as String}")

            runDir("build/datagen")
        }
        named("client") {
            programArgs += listOf("--username", "Player1")
        }
        named("server") {
            runDir = "run_server" // ファイルロックを回避しクライアントと同時に起動可能にする
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// 生成されたリソースを main ソースセットに追加する
sourceSets {
    main {
        java {
            srcDir("src/main/lib")
        }
        resources {
            srcDir("src/main/generated")
        }
    }
}

repositories {
    mavenCentral()

    // ここからアーティファクトを取得するリポジトリを追加します。
    // Loom は Minecraft とライブラリを自動的にダウンロードするために必要な Maven リポジトリを追加するため、
    // 他の mod に依存する場合にのみ使用してください。
    // リポジトリの詳細については、
    // https://docs.gradle.org/current/userguide/declaring_repositories.html を参照してください。

    // cloth-config-fabric
    //// me.shedaniel:RoughlyEnoughItems-*
    maven("https://maven.shedaniel.me/")

    // FauxCustomEntityData-fabric-1.19.2
    maven("https://maven.blamejared.com")

    // dev.emi:trinkets
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.ladysnake.org/releases")

}

dependencies {

    // バージョンを変更するには、gradle.properties ファイルを参照してください
    "minecraft"("com.mojang:minecraft:${project.property("minecraft_version") as String}")
    "mappings"("net.fabricmc:yarn:${project.property("yarn_mappings") as String}:v2")
    "modImplementation"("net.fabricmc:fabric-loader:${project.property("loader_version") as String}")

    // Fabric API。 これは技術的にはオプションですが、とにかく必要になるでしょう。
    "modImplementation"("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version") as String}")
    "modImplementation"("net.fabricmc:fabric-language-kotlin:${project.property("fabric_kotlin_version") as String}")

    // 非推奨の Fabric API モジュールを有効にするには、次の行のコメントを外します。
    // これらは Fabric API のプロダクション ディストリビューションに含まれており、後でより都合の良いときに mod を最新のモジュールに更新できます。
    //modImplementation "net.fabricmc.fabric-api:fabric-api-deprecated:${project.fabric_version}"


    "modApi"("me.shedaniel.cloth:cloth-config-fabric:8.2.88") {
        exclude(group = "net.fabricmc.fabric-api")
    }

    val reiVersion = "9.1.591"
    //modRuntimeOnly "me.shedaniel:RoughlyEnoughItems-fabric:$rei_version"
    "modCompileOnly"("me.shedaniel:RoughlyEnoughItems-api-fabric:$reiVersion")
    "modCompileOnly"("me.shedaniel:RoughlyEnoughItems-default-plugin-fabric:$reiVersion")

    "modApi"("com.faux.fauxcustomentitydata:FauxCustomEntityData-fabric-1.19.2:2.0.2")

    "modApi"("dev.emi:trinkets:3.4.1")
    // https://github.com/emilyploszaj/trinkets/blob/3.4.1/gradle.properties
    "modApi"("dev.onyxstudios.cardinal-components-api:cardinal-components-base:5.0.0-beta.1")

}

tasks {

    named<Copy>("processResources") {
        inputs.property("version", project.version)
        exclude("**/*.pdn")
        exclude("**/*.scr.png")

        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to project.version))
        }
    }

    withType<JavaCompile>().configureEach {
        options.release.set(17)
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    named<Jar>("jar") {
        from("LICENSE") {
            rename { "${it}_${project.base.archivesName}" }
        }
        archiveBaseName.set(project.property("archive_base_name") as String)
    }

}

java {
    // Loom は自動的に sourcesJar を RemapSourcesJar タスクにアタッチし、存在する場合は「build」タスクにアタッチします。
    // この行を削除すると、ソースは生成されません。
    withSourcesJar()
}

// maven パブリケーションを構成する
publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = project.property("maven_artifact_id") as String
            groupId = project.property("maven_group_id") as String
        }
    }

    // パブリッシングの設定方法については、 https://docs.gradle.org/current/userguide/publishing_maven.html を参照してください。
    repositories {
        // 公開するリポジトリをここに追加します。
        // 注意: このブロックには、最上位のブロックと同じ機能はありません。
        // ここのリポジトリは、依存関係を取得するためではなく、アーティファクトを公開するために使用されます。
    }
}
