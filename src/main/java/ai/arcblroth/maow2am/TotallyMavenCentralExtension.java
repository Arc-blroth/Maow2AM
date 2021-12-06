// Copyright 2021 Arc'blroth
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package ai.arcblroth.maow2am;

import groovy.lang.Closure;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * An extension that adds a totally legit
 * {@code mavenCentral()} method.
 */
public class TotallyMavenCentralExtension extends Closure<MavenArtifactRepository> implements Supplier<MavenArtifactRepository> {
    private final Project project;
    private final AtomicBoolean wasInvoked = new AtomicBoolean(false);

    /**
     * Constructs a new TotallyMavenCentralExtension, which
     * should be added to the DependencyHandler extensions.
     * @param project The enclosing project.
     */
    public TotallyMavenCentralExtension(Project project) {
        super(project.getDependencies());
        project.getGradle().buildFinished(buildResult -> {
            if (this.wasInvoked.get()) {
                project.getLogger().lifecycle("Deprecated features were used in this build, making it incompatible with Maow 2.0.");
            }
        });
        this.project = project;
    }

    /**
     * Adds a repository which looks in the Maven central repository for dependencies. The URL used to access this repository is
     * {@value org.gradle.api.artifacts.ArtifactRepositoryContainer#MAVEN_CENTRAL_URL}. The name of the repository is
     * {@value org.gradle.api.artifacts.ArtifactRepositoryContainer#DEFAULT_MAVEN_CENTRAL_REPO_NAME}.
     *
     * <p>Examples:</p>
     * <pre>
     * dependencies {
     *     mavenCentral()
     * }
     * </pre>
     *
     * @return the added resolver
     * @see org.gradle.api.artifacts.dsl.RepositoryHandler#mavenCentral(java.util.Map)
     */
    @Override
    public MavenArtifactRepository get() {
        this.wasInvoked.set(true);
        return this.project.getRepositories().mavenCentral();
    }

    @SuppressWarnings("unused")
    public MavenArtifactRepository doCall() {
        return get();
    }
}
