plugins {
    id 'fabric-loom' version '1.1-SNAPSHOT'
    id 'maven-publish'
    id "org.jetbrains.kotlin.jvm" version "1.8.0"
}

sourceCompatibility = JavaVersion.VERSION_17
targetCompatibility = JavaVersion.VERSION_17

archivesBaseName = project.archives_base_name
version = project.mod_version
group = project.maven_group

def rei_version = "9.1.591"

repositories {
    // ここからアーティファクトを取得するリポジトリを追加します。
    // Loom は Minecraft とライブラリを自動的にダウンロードするために必要な Maven リポジトリを追加するため、
    // 他の mod に依存する場合にのみ使用してください。
    // リポジトリの詳細については、
    // https://docs.gradle.org/current/userguide/declaring_repositories.html を参照してください。

    // cloth-config-fabric
    //// me.shedaniel:RoughlyEnoughItems-*
    maven { url "https://maven.shedaniel.me/" }

    // FauxCustomEntityData-fabric-1.19.2
    maven { url 'https://maven.blamejared.com' }

    // dev.emi:trinkets
    maven { url 'https://maven.terraformersmc.com/' }
    maven { url 'https://maven.ladysnake.org/releases' }

}

loom {
    splitEnvironmentSourceSets()

    mods {
        modid {
            sourceSet sourceSets.main
            sourceSet sourceSets.client
        }
    }
    runs {
        // これにより、datagen API を実行する新しい gradle タスク "gradlew runDatagen" が追加されます。
        datagen {
            inherit server
            name "Data Generation"
            vmArg "-Dfabric-api.datagen"
            vmArg "-Dfabric-api.datagen.output-dir=${file("src/main/generated")}"
            vmArg "-Dfabric-api.datagen.modid=${mod_id}"

            runDir "build/datagen"
        }
        client {
            programArgs.addAll("--username", "Player1")
        }
        server {
            runDir = "run_server" // ファイルロックを回避しクライアントと同時に起動可能にする
        }
    }
}

// 生成されたリソースを main ソースセットに追加する
sourceSets {
    main {
        java {
            srcDirs += [
                    'src/main/lib'
            ]
        }
        resources {
            srcDirs += [
                    'src/main/generated'
            ]
        }
    }
}

dependencies {

    // バージョンを変更するには、gradle.properties ファイルを参照してください
    minecraft "com.mojang:minecraft:${project.minecraft_version}"
    mappings "net.fabricmc:yarn:${project.yarn_mappings}:v2"
    modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"

    // Fabric API。 これは技術的にはオプションですが、とにかく必要になるでしょう。
    modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_version}"
    modImplementation "net.fabricmc:fabric-language-kotlin:${project.fabric_kotlin_version}"

    // 非推奨の Fabric API モジュールを有効にするには、次の行のコメントを外します。
    // これらは Fabric API のプロダクション ディストリビューションに含まれており、後でより都合の良いときに mod を最新のモジュールに更新できます。
    //modImplementation "net.fabricmc.fabric-api:fabric-api-deprecated:${project.fabric_version}"


    modApi("me.shedaniel.cloth:cloth-config-fabric:8.2.88") {
        exclude(group: "net.fabricmc.fabric-api")
    }

    //modRuntimeOnly "me.shedaniel:RoughlyEnoughItems-fabric:$rei_version"
    modCompileOnly "me.shedaniel:RoughlyEnoughItems-api-fabric:$rei_version"
    modCompileOnly "me.shedaniel:RoughlyEnoughItems-default-plugin-fabric:$rei_version"

    modApi "com.faux.fauxcustomentitydata:FauxCustomEntityData-fabric-1.19.2:2.0.2"

    modApi "dev.emi:trinkets:3.4.1"
    // https://github.com/emilyploszaj/trinkets/blob/3.4.1/gradle.properties
    modApi "dev.onyxstudios.cardinal-components-api:cardinal-components-base:5.0.0-beta.1"

}

processResources {
    inputs.property "version", project.version
    exclude "**/*.pdn"
    exclude "**/*.scr.png"

    filesMatching("fabric.mod.json") {
        expand "version": project.version
    }
}

tasks.withType(JavaCompile).configureEach {
    it.options.release = 17
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).all {
    kotlinOptions {
        jvmTarget = 17
    }
}

java {
    // Loom は自動的に sourcesJar を RemapSourcesJar タスクにアタッチし、存在する場合は「build」タスクにアタッチします。
    // この行を削除すると、ソースは生成されません。
    withSourcesJar()
}

jar {
    from("LICENSE") {
        rename { "${it}_${project.archivesBaseName}" }
    }
}

// maven パブリケーションを構成する
publishing {
    publications {
        mavenJava(MavenPublication) {
            from components.java
        }
    }

    // パブリッシングの設定方法については、 https://docs.gradle.org/current/userguide/publishing_maven.html を参照してください。
    repositories {
        // 公開するリポジトリをここに追加します。
        // 注意: このブロックには、最上位のブロックと同じ機能はありません。
        // ここのリポジトリは、依存関係を取得するためではなく、アーティファクトを公開するために使用されます。
    }
}