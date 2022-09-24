using DiscussionService.Models;

namespace DiscussionService.Contracts
{
    public interface ILecturerRepository : IRepositoryBase<Lecturer>
    {
        Task<IEnumerable<Lecturer>> GetAllLecturersAsync();
        Task<Lecturer> GetLecturerByIdAsync(Guid lecturerId);
        Task<Lecturer> GetLecturerByEmailAsync(string lecturerEmail);
        void CreateLecturer(Lecturer lecturer);
        void UpdateLecturer(Lecturer lecturer);
        void DeleteLecturer(Lecturer lecturer);
    }
}