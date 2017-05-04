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
INSERT INTO `code` ( `del_flag`, `version`, `code`, `description`, `type`) VALUES
	( 'N', 0, 'TEXT', 'Text', 'SERVICE_REQUEST'),
	( 'N', 1, 'ACC', 'Account', 'SERVICE_REQUEST'),
	( 'N', 0, 'DATE', 'Date', 'SERVICE_REQUEST'),
	( 'N', 0, 'FI', 'Financial Institutions', 'SERVICE_REQUEST'),
	( 'N', 0, 'LIST', 'Fixed List', 'SERVICE_REQUEST'),
	( 'N', 0, 'US', 'United States of America', 'USA'),
	( 'Y', 1, 'NG', 'Nigeria', 'Nigeria'),
	( 'N', 1, 'NG', 'Nigeria', 'COUNTRY'),
	( 'N', 0, 'REQ', 'Request', 'REQUEST_TYPE'),
	( 'N', 0, 'COMPL', 'Issue', 'REQUEST_TYPE'),
	( 'N', 0, 'CONTR', 'Control', 'UNIT'),
	( 'N', 0, 'OPS', 'Operations', 'UNIT'),
	( 'N', 0, 'CS', 'Customer Service', 'UNIT'),
	( 'N', 0, 'RUS', 'Russia', 'COUNTRY'),
	( 'N', 1, 'CAME', 'Cameroon', 'COUNTRY'),
	( 'N', 0, 'S', 'Submitted', 'REQUEST_STATUS'),
	( 'N\r\n', 0, 'P', 'Pending', 'REQUEST_STATUS'),
	( 'N', 0, 'R', 'Resolved', 'REQUEST_STATUS'),
	( 'N', 0, 'T', 'Treated', 'REQUEST_STATUS'),
	( 'N', 0, 'C', 'Collected', 'REQUEST_STATUS'),
	( 'N', 0, 'NIP', 'NIP', 'TRANSFER_CHANNEL'),
	( 'N', 0, 'NAPS', 'NAPS', 'TRANSFER_CHANNEL'),
	( 'N', 0, 'RTGS', 'RTGS', 'TRANSFER_CHANNEL'),
	( 'N', 0, 'CMB', 'CMB', 'TRANSFER_CHANNEL'),
	( 'N', 0, 'NGN', 'Naira', 'CURRENCY'),
	( 'N', 0, 'USD', 'US Dollar', 'CURRENCY'),
	( 'N', 0, 'GBP', 'British Pound', 'CURRENCY'),
	( 'N', 0, 'D', 'Daily', 'FREQUENCY'),
	( 'N', 0, 'W', 'Weekly', 'FREQUENCY'),
	( 'N', 0, 'M', 'Monthly', 'FREQUENCY'),
	( 'N', 0, 'Q', 'Quarterly', 'FREQUENCY'),
	( 'N', 0, 'B', 'Bi-Annually', 'FREQUENCY'),
	( 'N', 0, 'A', 'Annually', 'FREQUENCY'),
	( 'N', 0, 'SBA', 'SBA', 'ACCOUNT_CLASS'),
	( 'N', 0, 'ODA', 'ODA', 'ACCOUNT_CLASS'),
	( 'N', 0, 'BOTH', 'SMS and Email', 'ALERT_PREFERENCE'),
	( 'N', 0, 'SMS', 'SMS only', 'ALERT_PREFERENCE'),
	( 'N', 0, 'EMAIL', 'Email only', 'ALERT_PREFERENCE'),
	( 'N', 0, 'NONE', 'No alerts', 'ALERT_PREFERENCE');
	( 'N', 0, 'TEXTAREA', 'Text Area', 'SERVICE_REQUEST'),
	( 'N', 0, 'CODE', 'Codes', 'SERVICE_REQUEST'),
/*!40000 ALTER TABLE `code` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
