package com.genius.markworkingdaysapp.data.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AppDatabaseMigration2To3(
    private val dailyRate: Int
) : Migration(2, 3) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                CREATE TABLE `work_days_new` (
                `epochDay` INTEGER NOT NULL,
                `status` TEXT NOT NULL,
                `bonus` INTEGER,
                `earned` INTEGER NOT NULL,
                `note` TEXT,
                PRIMARY KEY(`epochDay`)
                )
            """.trimIndent()
        )

        db.execSQL(
            """
                INSERT INTO `work_days_new` (
                `epochDay`,
                `status`,
                `bonus`,
                `earned`,
                `note`
            )
            SELECT
                `epochDay`,
                CASE
                    WHEN `shortDayEarned` IS NOT NULL THEN 'SHORT_DAY'
                    WHEN `worked` = 1 THEN 'FULL_DAY'
                    ELSE 'NOT_WORKED'
                END,
                CASE
                    WHEN `worked` = 1
                        AND `shortDayEarned` IS NULL
                    THEN `bonus`
                    ELSE NULL
                END,
                CASE
                    WHEN `shortDayEarned` IS NOT NULL
                    THEN `shortDayEarned`
                    
                    WHEN `worked` = 1
                    THEN ? + COALESCE(`bonus`, 0)
                    
                    ELSE 0
                END,
                `note`
            FROM `work_days`
            """.trimIndent(),
            arrayOf<Any?>(
                dailyRate,
            )
        )

        db.execSQL("DROP TABLE `work_days`")

        db.execSQL(
            "ALTER TABLE `work_days_new` RENAME TO `work_days`"
        )

        db.execSQL("""
            CREATE TABLE `month_rates` (
            `monthStartEpochDay` INTEGER NOT NULL,
            `dailyRate` INTEGER NOT NULL,
            PRIMARY KEY(`monthStartEpochDay`)
            )
        """.trimIndent()
        )
        db.execSQL(
            """
                INSERT INTO `month_rates` (
                    `monthStartEpochDay`,
                    `dailyRate`
                )
                SELECT DISTINCT
                    CAST (
                        strftime(
                            '%s',
                            `epochDay` * 86400,
                            'unixepoch',
                            'start of month'
                        ) AS INTEGER
                    ) / 86400,
                    ?
                FROM `work_days`
            """.trimIndent(),
            arrayOf<Any?>(dailyRate)
        )



    }

}