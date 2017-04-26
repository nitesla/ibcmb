-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.5.53 - MySQL Community Server (GPL)
-- Server OS:                    Win32
-- HeidiSQL Version:             9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;




-- Dumping structure for table ibanking.code
CREATE TABLE IF NOT EXISTS `code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `del_flag` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKcixp4r2mkj9tcju6rqoxxcxfl` (`code`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;

-- Dumping data for table ibanking.code: ~34 rows (approximately)
/*!40000 ALTER TABLE `code` DISABLE KEYS */;
INSERT INTO `code` (`id`, `del_flag`, `version`, `code`, `description`, `type`) VALUES
	(1, 'N', 0, 'TEXT', 'Text', 'SERVICE_REQUEST'),
	(2, 'N', 1, 'ACC', 'Account', 'SERVICE_REQUEST'),
	(3, 'N', 0, 'DATE', 'Date', 'SERVICE_REQUEST'),
	(5, 'N', 0, 'LIST', 'Fixed List', 'SERVICE_REQUEST'),
	(6, 'N', 0, 'US', 'United States of America', 'USA'),
	(7, 'Y', 1, 'NG', 'Nigeria', 'Nigeria'),
	(8, 'N', 1, 'NG', 'Nigeria', 'COUNTRY'),
	(9, 'N', 0, 'REQ', 'Request', 'REQUEST_TYPE'),
	(10, 'N', 0, 'COMPL', 'Issue', 'REQUEST_TYPE'),
	(11, 'N', 0, 'CONTR', 'Control', 'UNIT'),
	(12, 'N', 0, 'OPS', 'Operations', 'UNIT'),
	(13, 'N', 0, 'CS', 'Customer Service', 'UNIT'),
	(14, 'N', 0, 'RUS', 'Russia', 'COUNTRY'),
	(18, 'N', 1, 'CAME', 'Cameroon', 'COUNTRY'),
	(19, 'N', 0, 'S', 'Submitted', 'REQUEST_STATUS'),
	(20, 'N\r\n', 0, 'P', 'Pending', 'REQUEST_STATUS'),
	(21, 'N', 0, 'R', 'Resolved', 'REQUEST_STATUS'),
	(22, 'N', 0, 'T', 'Treated', 'REQUEST_STATUS'),
	(23, 'N', 0, 'C', 'Collected', 'REQUEST_STATUS'),
	(24, 'N', 0, 'NIP', 'NIP', 'TRANSFER_CHANNEL'),
	(25, 'N', 0, 'NAPS', 'NAPS', 'TRANSFER_CHANNEL'),
	(26, 'N', 0, 'RTGS', 'RTGS', 'TRANSFER_CHANNEL'),
	(27, 'N', 0, 'CMB', 'CMB', 'TRANSFER_CHANNEL'),
	(28, 'N', 0, 'NGN', 'Naira', 'CURRENCY'),
	(29, 'N', 0, 'USD', 'US Dollar', 'CURRENCY'),
	(30, 'N', 0, 'GBP', 'British Pound', 'CURRENCY'),
	(31, 'N', 0, 'D', 'Daily', 'FREQUENCY'),
	(32, 'N', 0, 'W', 'Weekly', 'FREQUENCY'),
	(33, 'N', 0, 'M', 'Monthly', 'FREQUENCY'),
	(34, 'N', 0, 'Q', 'Quarterly', 'FREQUENCY'),
	(35, 'N', 0, 'B', 'Bi-Annually', 'FREQUENCY'),
	(36, 'N', 0, 'A', 'Annually', 'FREQUENCY'),
	(37, 'N', 0, 'SBA', 'SBA', 'ACCOUNT_CLASS'),
	(38, 'N', 0, 'ODA', 'ODA', 'ACCOUNT_CLASS');
/*!40000 ALTER TABLE `code` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
