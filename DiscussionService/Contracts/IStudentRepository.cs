using DiscussionService.Models;

namespace DiscussionService.Contracts
{
    public interface IStudentRepository : IRepositoryBase<Student>
    {
        Task<IEnumerable<Student>> GetAllStudentsAsync();
        Task<Student> GetStudentByIdAsync(Guid studentId);
        Task<Student> GetStudentByEmailAsync(string studentEmail);
        void CreateStudent(Student student);
        void UpdateStudent(Student student);
        void DeleteStudent(Student student);
    }
}