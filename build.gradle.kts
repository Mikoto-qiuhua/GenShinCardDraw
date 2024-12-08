plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
}

group = "org.qiuhua.genshincarddraw"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()  //加载本地仓库
    mavenCentral()  //加载中央仓库
    maven {
        name = "spigotmc-repo"
        url = uri ("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }  //SpigotMC仓库
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    compileOnly("com.google.code.gson:gson:2.10.1")
    compileOnly("com.zaxxer:HikariCP:4.0.3")//数据库连接池
    compileOnly("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")  //仅在编译时可用
    compileOnly("org.apache.commons:commons-math3:3.6.1")
    compileOnly(fileTree("src/libs"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<Jar>().configureEach {
    archiveFileName.set("GenShinCardDraw-1.0.0.jar")
    destinationDirectory.set(File ("D:\\我的世界插件"))
}

tasks.withType<JavaCompile>{
    options.encoding = "UTF-8"
}