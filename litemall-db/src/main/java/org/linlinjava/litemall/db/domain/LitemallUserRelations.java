package org.linlinjava.litemall.db.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class LitemallUserRelations {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_user_relations.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_user_relations.fxLevel
     *
     * @mbg.generated
     */
    private Long fxlevel;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_user_relations.userid
     *
     * @mbg.generated
     */
    private Long userid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_user_relations.childid
     *
     * @mbg.generated
     */
    private Long childid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_user_relations.created
     *
     * @mbg.generated
     */
    private LocalDateTime created;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_user_relations.id
     *
     * @return the value of litemall_user_relations.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_user_relations.id
     *
     * @param id the value for litemall_user_relations.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_user_relations.fxLevel
     *
     * @return the value of litemall_user_relations.fxLevel
     *
     * @mbg.generated
     */
    public Long getFxlevel() {
        return fxlevel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_user_relations.fxLevel
     *
     * @param fxlevel the value for litemall_user_relations.fxLevel
     *
     * @mbg.generated
     */
    public void setFxlevel(Long fxlevel) {
        this.fxlevel = fxlevel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_user_relations.userid
     *
     * @return the value of litemall_user_relations.userid
     *
     * @mbg.generated
     */
    public Long getUserid() {
        return userid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_user_relations.userid
     *
     * @param userid the value for litemall_user_relations.userid
     *
     * @mbg.generated
     */
    public void setUserid(Long userid) {
        this.userid = userid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_user_relations.childid
     *
     * @return the value of litemall_user_relations.childid
     *
     * @mbg.generated
     */
    public Long getChildid() {
        return childid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_user_relations.childid
     *
     * @param childid the value for litemall_user_relations.childid
     *
     * @mbg.generated
     */
    public void setChildid(Long childid) {
        this.childid = childid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_user_relations.created
     *
     * @return the value of litemall_user_relations.created
     *
     * @mbg.generated
     */
    public LocalDateTime getCreated() {
        return created;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_user_relations.created
     *
     * @param created the value for litemall_user_relations.created
     *
     * @mbg.generated
     */
    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", fxlevel=").append(fxlevel);
        sb.append(", userid=").append(userid);
        sb.append(", childid=").append(childid);
        sb.append(", created=").append(created);
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        LitemallUserRelations other = (LitemallUserRelations) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getFxlevel() == null ? other.getFxlevel() == null : this.getFxlevel().equals(other.getFxlevel()))
            && (this.getUserid() == null ? other.getUserid() == null : this.getUserid().equals(other.getUserid()))
            && (this.getChildid() == null ? other.getChildid() == null : this.getChildid().equals(other.getChildid()))
            && (this.getCreated() == null ? other.getCreated() == null : this.getCreated().equals(other.getCreated()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getFxlevel() == null) ? 0 : getFxlevel().hashCode());
        result = prime * result + ((getUserid() == null) ? 0 : getUserid().hashCode());
        result = prime * result + ((getChildid() == null) ? 0 : getChildid().hashCode());
        result = prime * result + ((getCreated() == null) ? 0 : getCreated().hashCode());
        return result;
    }

    /**
     * This enum was generated by MyBatis Generator.
     * This enum corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    public enum Column {
        id("id", "id", "BIGINT", false),
        fxlevel("fxLevel", "fxlevel", "BIGINT", false),
        userid("userid", "userid", "BIGINT", false),
        childid("childid", "childid", "BIGINT", false),
        created("created", "created", "TIMESTAMP", false);

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table litemall_user_relations
         *
         * @mbg.generated
         */
        private static final String BEGINNING_DELIMITER = "`";

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table litemall_user_relations
         *
         * @mbg.generated
         */
        private static final String ENDING_DELIMITER = "`";

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table litemall_user_relations
         *
         * @mbg.generated
         */
        private final String column;

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table litemall_user_relations
         *
         * @mbg.generated
         */
        private final boolean isColumnNameDelimited;

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table litemall_user_relations
         *
         * @mbg.generated
         */
        private final String javaProperty;

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table litemall_user_relations
         *
         * @mbg.generated
         */
        private final String jdbcType;

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_user_relations
         *
         * @mbg.generated
         */
        public String value() {
            return this.column;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_user_relations
         *
         * @mbg.generated
         */
        public String getValue() {
            return this.column;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_user_relations
         *
         * @mbg.generated
         */
        public String getJavaProperty() {
            return this.javaProperty;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_user_relations
         *
         * @mbg.generated
         */
        public String getJdbcType() {
            return this.jdbcType;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_user_relations
         *
         * @mbg.generated
         */
        Column(String column, String javaProperty, String jdbcType, boolean isColumnNameDelimited) {
            this.column = column;
            this.javaProperty = javaProperty;
            this.jdbcType = jdbcType;
            this.isColumnNameDelimited = isColumnNameDelimited;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_user_relations
         *
         * @mbg.generated
         */
        public String desc() {
            return this.getEscapedColumnName() + " DESC";
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_user_relations
         *
         * @mbg.generated
         */
        public String asc() {
            return this.getEscapedColumnName() + " ASC";
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_user_relations
         *
         * @mbg.generated
         */
        public static Column[] excludes(Column ... excludes) {
            ArrayList<Column> columns = new ArrayList<>(Arrays.asList(Column.values()));
            if (excludes != null && excludes.length > 0) {
                columns.removeAll(new ArrayList<>(Arrays.asList(excludes)));
            }
            return columns.toArray(new Column[]{});
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_user_relations
         *
         * @mbg.generated
         */
        public String getEscapedColumnName() {
            if (this.isColumnNameDelimited) {
                return new StringBuilder().append(BEGINNING_DELIMITER).append(this.column).append(ENDING_DELIMITER).toString();
            } else {
                return this.column;
            }
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_user_relations
         *
         * @mbg.generated
         */
        public String getAliasedEscapedColumnName() {
            return this.getEscapedColumnName();
        }
    }
}