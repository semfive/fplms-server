/*
  Warnings:

  - The primary key for the `tblNotifications` table will be changed. If it partially fails, the table could be left without primary key constraint.
  - You are about to drop the column `Id` on the `tblNotifications` table. All the data in the column will be lost.
  - The required column `id` was added to the `tblNotifications` table with a prisma-level default value. This is not possible if the table is not empty. Please add this column as optional, then populate it before making it required.

*/
-- AlterTable
ALTER TABLE `tblNotifications` DROP PRIMARY KEY,
    DROP COLUMN `Id`,
    ADD COLUMN `id` VARCHAR(191) NOT NULL,
    ADD PRIMARY KEY (`id`);
