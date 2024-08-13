/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2023. All rights reserved.
 */

package com.huawei.fitframework.plugin.maven.support;

import com.huawei.fitframework.plugin.maven.MavenCoordinate;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * The DefaultMavenCoordinate
 *
 * @author 陈镕希
 * @since 2020/12/26
 */
@Getter
@AllArgsConstructor
public class DefaultMavenCoordinate implements MavenCoordinate {
    private final String groupId;
    private final String artifactId;
    private final String version;

    @Override
    public String toString() {
        return this.getGroupId() + ':' + this.getArtifactId() + ':' + this.getVersion();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof MavenCoordinate) {
            MavenCoordinate another = (MavenCoordinate) obj;
            return another.getGroupId().equals(this.getGroupId()) && another.getArtifactId()
                    .equals(this.getArtifactId()) && another.getVersion().equals(this.getVersion());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                this.getGroupId(), this.getArtifactId(), this.getVersion()
        });
    }

    /**
     * {@link MavenCoordinate.Builder} 的具体实现。
     */
    public static class Builder implements MavenCoordinate.Builder {
        private String groupId;
        private String artifactId;
        private String version;

        public Builder(MavenCoordinate coordinate) {
            if (coordinate != null) {
                this.groupId = coordinate.getGroupId();
                this.artifactId = coordinate.getArtifactId();
                this.version = coordinate.getVersion();
            }
        }

        @Override
        public Builder setGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        @Override
        public Builder setArtifactId(String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        @Override
        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        @Override
        public MavenCoordinate build() {
            return new DefaultMavenCoordinate(this.groupId, this.artifactId, this.version);
        }
    }
}
